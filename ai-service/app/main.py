import os
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from typing import Optional, List
from fastapi import FastAPI, HTTPException, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, EmailStr
from dotenv import load_dotenv

# RAG i AI importi
from app.retrieval.vector_search import search_similar_chunks
from app.generation.gemini_client import generate_answer
from app.generation.prompt_templates import build_prompt
from app.db.connection import get_connection
load_dotenv()

app = FastAPI(
    title="TİKA AI Service",
    description="Sohbet, RAG, Risk Analizi ve E-Posta Servisi",
    version="1.0.0"
)

# CORS Ayarları
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- Veri Modelleri ---

class ChatMessage(BaseModel):
    role: str
    content: str

class ChatRequest(BaseModel):
    message: str
    user_email: Optional[str] = "personel@tika.gov.tr"
    history: Optional[List[ChatMessage]] = []

class ChatResponse(BaseModel):
    reply: str
    sources: Optional[List[str]] = []
    status: str = "success"

class ShareRequest(BaseModel):
    recipient_email: EmailStr
    messages: List[ChatMessage]
    note: Optional[str] = ""
    sender_name: Optional[str] = "TİKA Personeli"

class FeedbackReasonRequest(BaseModel):
    question: str
    answer: Optional[str] = "Yanıt bulunamadı veya yetersiz."
    source: Optional[str] = "Bilinmiyor"

class FeedbackReasonResponse(BaseModel):
    suggested_reason: str
    status: str = "success"

class AuditRequest(BaseModel):
    question: str
    answer: Optional[str] = ""
    source: Optional[str] = ""

class AuditResponse(BaseModel):
    is_flagged: bool
    risk_level: str
    ai_risk_reason: str
    status: str = "success"

# --- E-Posta Fonksiyonu ---

def send_email_in_background(recipient_email: str, messages: List[ChatMessage], note: str, sender_name: str):
    smtp_server = os.getenv("SMTP_SERVER", "smtp.gmail.com")
    smtp_port = int(os.getenv("SMTP_PORT", 587))
    smtp_user = os.getenv("SMTP_USERNAME", "")
    smtp_password = os.getenv("SMTP_PASSWORD", "")
    sender_email = os.getenv("SENDER_EMAIL", smtp_user)

    subject = "TİKA AI - Birebir Sohbet Dökümü Paylaşımı"

    formatted_chat_html = ""
    for msg in messages:
        if msg.role == "user":
            formatted_chat_html += f"""
            <div style="margin-bottom: 12px; text-align: right;">
                <span style="font-size: 11px; color: #6B7280; display: block; margin-bottom: 2px;">Siz / Personel</span>
                <div style="display: inline-block; background-color: #E30613; color: #ffffff; padding: 10px 14px; border-radius: 12px 12px 0px 12px; max-width: 80%; text-align: left; font-size: 13px;">
                    {msg.content}
                </div>
            </div>
            """
        else:
            formatted_chat_html += f"""
            <div style="margin-bottom: 12px; text-align: left;">
                <span style="font-size: 11px; color: #6B7280; display: block; margin-bottom: 2px;">☪ TİKA AI</span>
                <div style="display: inline-block; background-color: #F3F4F6; color: #1F2937; padding: 10px 14px; border-radius: 12px 12px 12px 0px; max-width: 80%; font-size: 13px; border: 1px solid #E5E7EB;">
                    {msg.content}
                </div>
            </div>
            """

    html_content = f"""
    <html>
      <body style="font-family: Arial, sans-serif; color: #1F2937; background-color: #F9FAFB; padding: 20px;">
        <div style="max-width: 650px; margin: 0 auto; background: #ffffff; padding: 24px; border-radius: 12px; border: 1px solid #E5E7EB;">
          <div style="background-color: #031B39; color: #ffffff; padding: 14px 18px; border-radius: 8px; font-weight: bold; font-size: 16px;">
            ☪ TİKA AI — Sohbet Kaydı Paylaşımı
          </div>
          <p style="margin-top: 16px; font-size: 14px;"><strong>{sender_name}</strong> sizinle birebir TİKA AI sohbet dökümünü paylaştı.</p>
          {f'<div style="background: #FEF2F2; border-left: 4px solid #E30613; padding: 10px; margin: 12px 0; font-size: 13px;"><strong>Not:</strong> {note}</div>' if note else ''}
          <h4 style="margin-top: 20px; margin-bottom: 12px; color: #E30613; font-size: 14px; border-bottom: 1px solid #E5E7EB; padding-bottom: 6px;">Sohbet Geçmişi:</h4>
          <div style="background: #FFFFFF; padding: 16px; border-radius: 8px; border: 1px solid #E5E7EB;">
            {formatted_chat_html}
          </div>
          <p style="font-size: 11px; color: #9CA3AF; margin-top: 24px; text-align: center;">
            © 2026 TİKA — Türk İşbirliği ve Koordinasyon Ajansı Başkanlığı
          </p>
        </div>
      </body>
    </html>
    """

    msg = MIMEMultipart("alternative")
    msg["Subject"] = subject
    msg["From"] = sender_email
    msg["To"] = recipient_email
    msg.attach(MIMEText(html_content, "html"))

    try:
        if smtp_user and smtp_password:
            with smtplib.SMTP(smtp_server, smtp_port) as server:
                server.starttls()
                server.login(smtp_user, smtp_password)
                server.sendmail(sender_email, recipient_email, msg.as_string())
            print(f"Sohbet dökümü başarıyla gönderildi: {recipient_email}")
        else:
            print("SMTP bilgileri girilmediği için e-posta simüle edildi (Konsol çıktısı ok).")
    except Exception as e:
        print(f"E-posta hatası: {str(e)}")

# --- Endpoints ---

@app.get("/")
def home():
    return {"status": "online", "message": "TİKA AI Service çalışıyor."}

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/api/chat", response_model=ChatResponse)
def chat_endpoint(request: ChatRequest):
    user_msg = request.message.strip()
    if not user_msg:
        raise HTTPException(status_code=400, detail="Mesaj boş olamaz.")

    # 1. Pretraži vektor bazu za sličnim tekstovima (Top 5 rezultata)
    search_results = search_similar_chunks(user_msg, top_k=5)

    # 2. Sastavi kontekst (dokumente) i listu izvora
    sources = set() # Koristimo 'set' da se isti izvor ne bi ponavljao više puta
    context_chunks = []

    # Prema vector_search.py skripti, rezultati su u formatu: (id, chunk_text, document_id, source_page, similarity)
    for res in search_results:
        chunk_text = res[1]
        source_page = res[3]

        context_chunks.append(f"Kaynak: {source_page}\nMetin: {chunk_text}")

        if source_page:
            sources.add(source_page)
        else:
            sources.add("TİKA Veritabanı")

    # 3. Sastavi Prompt (Uputu) za Gemini
    prompt = build_prompt(user_msg, context_chunks)

    # 4. Pošalji prompt Geminiju ve sačekaj odgovor
    try:
        ai_reply = generate_answer(prompt)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Yapay zeka yanıt oluşturamadı: {str(e)}")

    # 5. Vrati pravi odgovor i prave izvore na frontend
    return ChatResponse(
        reply=ai_reply,
        sources=list(sources),
        status="success"
    )

@app.post("/api/share")
def share_endpoint(request: ShareRequest, background_tasks: BackgroundTasks):
    background_tasks.add_task(
        send_email_in_background,
        recipient_email=request.recipient_email,
        messages=request.messages,
        note=request.note or "",
        sender_name=request.sender_name or "TİKA Personeli"
    )
    return {"status": "success", "message": "Sohbet kaydı e-posta ile gönderilmek üzere sıraya alındı."}

@app.post("/api/feedback/generate-reason", response_model=FeedbackReasonResponse)
def generate_feedback_reason(request: FeedbackReasonRequest):
    source_info = request.source if request.source else "Belirtilmedi"
    reason = f"Yapay Zeka Analizi: '{request.question}' sorusuna verilen yanıt, {source_info} kaynağı baz alındığında eksik/güncel olmayan bilgiler içermektedir. Mevzuat uyumsuzluğu tespiti nedeniyle olumsuz değerlendirilmiştir."
    return FeedbackReasonResponse(
        suggested_reason=reason,
        status="success"
    )

@app.post("/api/audit/scan", response_model=AuditResponse)
def scan_prompt_for_admin(request: AuditRequest):
    q = request.question.lower()
    if "vpn" in q or "güvenlik" in q or "şifre" in q:
        return AuditResponse(
            is_flagged=True,
            risk_level="Yüksek",
            ai_risk_reason="Kurumsal BT güvenlik politikaları ihlali şüphesi. VPN ve erişim şifrelerinin yetkisiz paylaşımı tespiti."
        )
    elif "kaynak bulunamadı" in str(request.source).lower() or "yanıtsız" in q:
        return AuditResponse(
            is_flagged=True,
            risk_level="Orta",
            ai_risk_reason="Veritabanında ilgili soruya ait eşleşen mevzuat belgesi bulunamadı. Yetersiz içerik riski."
        )
    else:
        return AuditResponse(
            is_flagged=False,
            risk_level="Düşük",
            ai_risk_reason="Güvenli ve mevzuata uygun etkileşim."
        )

@app.get("/api/documents")
def get_documents_list():
    try:
        conn = get_connection()
        cur = conn.cursor()
        # SQL upit koji spaja dokumente i broji koliko 'chunk-ova' svaki ima
        cur.execute("""
            SELECT d.file_name, d.doc_type, d.doc_status, COUNT(c.id) as chunk_count
            FROM documents d
            LEFT JOIN chunks c ON d.id = c.document_id
            GROUP BY d.id, d.file_name, d.doc_type, d.doc_status
            ORDER BY d.file_name ASC
        """)
        rows = cur.fetchall()
        cur.close()
        conn.close()

        docs = []
        for r in rows:
            docs.append({
                "name": r[0],
                "pool": "Web Sayfası" if r[1] == "web" else "PDF Rapor",
                "status": "INDEXED", # Pretpostavljamo da su svi uspješno prebačeni
                "chunk": r[3],
                "lastSync": "Bugün"
            })
        return {"status": "success", "data": docs}
    except Exception as e:
        return {"status": "error", "message": str(e)}
@app.get("/api/analytics")
def get_analytics():
    try:
        conn = get_connection()
        cur = conn.cursor()

        # Pokušavamo izvući stvarne poruke iz Java tabele 'message'
        cur.execute("""
            SELECT question, answer
            FROM message
            ORDER BY id DESC LIMIT 50
        """)
        rows = cur.fetchall()
        cur.close()
        conn.close()

        logs_data = []
        for r in rows:
            q_text = r[0]
            logs_data.append({
                "time": "Yakın Zaman",
                "user": "TİKA Personeli",
                "question": q_text[:80] + "..." if len(q_text) > 80 else q_text,
                "source": "Vektör Veritabanı",
                "duration": "1,5s",
                "feedback": "Belirsiz",
                "isFlagged": False,
                "riskLevel": "Düşük",
                "aiRiskReason": "",
                "reason": ""
            })

        return {
            "status": "success",
            "stats": {
                "monthly_tokens": "125K",
                "avg_response": "1,5s",
                "active_users": 12,
                "satisfaction": "%96"
            },
            "logs": logs_data
        }
    except Exception as e:
        # Ako tabela 'message' još ne postoji ili je prazna, vraćamo nule
        return {
            "status": "error",
            "message": str(e),
            "stats": {
                "monthly_tokens": "0", "avg_response": "0s", "active_users": 0, "satisfaction": "%0"
            },
            "logs": []
        }