import os
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from typing import Optional, List
from fastapi import FastAPI, HTTPException, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, EmailStr
from dotenv import load_dotenv

load_dotenv()

app = FastAPI(
    title="TİKA AI Service",
    description="Sohbet ve E-Posta Paylaşım Uçları",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://127.0.0.1:5173"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- Veri Modelleri ---

class ChatMessage(BaseModel):
    role: str  # "user" veya "assistant"
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
    messages: List[ChatMessage]  # Birebir sohbet geçmişi
    note: Optional[str] = ""
    sender_name: Optional[str] = "TİKA Personeli"


# --- Risk Taraması & Feedback Modelleri ---

class AuditRequest(BaseModel):
    question: str
    answer: Optional[str] = ""
    source: Optional[str] = ""

class AuditResponse(BaseModel):
    is_flagged: bool  # True ise admin paneline düşer
    risk_level: str   # "Yüksek", "Orta", "Düşük"
    ai_risk_reason: str # AI'ın tespit ettiği olumsuzluk nedeni
    status: str = "success"

# --- Arka Plan E-Posta Gönderim Fonksiyonu ---

def send_email_in_background(recipient_email: str, messages: List[ChatMessage], note: str, sender_name: str):
    smtp_server = os.getenv("SMTP_SERVER", "smtp.gmail.com")
    smtp_port = int(os.getenv("SMTP_PORT", 587))
    smtp_user = os.getenv("SMTP_USERNAME", "")
    smtp_password = os.getenv("SMTP_PASSWORD", "")
    sender_email = os.getenv("SENDER_EMAIL", smtp_user)

    subject = "TİKA AI - Birebir Sohbet Dökümü Paylaşımı"

    # Sohbet mesajlarını HTML biçimine dönüştür
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
            print("SMTP bilgileri girilmediği için e-posta simüle edildi.")
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

    mock_response = f"Sorunuz alındı: '{user_msg}'. TİKA iç mevzuatı incelendi."
    mock_sources = ["Personel Yönetmeliği 2026.pdf (Sayfa 12)"]

    return ChatResponse(
        reply=mock_response,
        sources=mock_sources,
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

# --- AI Ön Tarama ve Risk Tespit Ucu ---
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