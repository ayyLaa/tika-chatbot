from dotenv import load_dotenv
load_dotenv()
import sys
sys.stdout.reconfigure(encoding='utf-8')

import os
os.environ["GEMINI_API_KEY"] = os.getenv("GEMINI_API_KEY_INGEST_JSON")  # or your main key

import uuid
from docx import Document
from app.ingestion.pipeline import process_document
from app.db.connection import get_connection


def document_already_exists(doc_path: str) -> bool:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT 1 FROM documents WHERE doc_path = %s AND doc_status = 'ready'", (doc_path,))
    exists = cur.fetchone() is not None
    cur.close()
    conn.close()
    return exists


def extract_docx_text(filepath: str) -> str:
    doc = Document(filepath)
    paragraphs = [p.text for p in doc.paragraphs if p.text.strip()]
    return "\n\n".join(paragraphs)


def ingest_local_document(title: str, text: str, doc_path: str):
    if document_already_exists(doc_path):
        print(f"SKIPPED (already exists): {title}")
        return

    document_id = str(uuid.uuid4())
    conn = get_connection()
    cur = conn.cursor()
    cur.execute(
        "INSERT INTO documents (id, file_name, doc_type, doc_path, doc_status) VALUES (%s, %s, %s, %s, %s)",
        (document_id, title, "internal", doc_path, "processing")
    )
    conn.commit()
    cur.close()
    conn.close()

    process_document(document_id, text, source_url=doc_path)
    print(f"OK: {title}")


if __name__ == "__main__":
    # 1. Hakkımızda.docx — has clean text, extract it directly
    hakkimizda_text = extract_docx_text("Hakkımızda.docx")
    ingest_local_document(
        title="Hakkımızda - TİKA (İç Doküman)",
        text=hakkimizda_text,
        doc_path="internal:hakkimizda-docx"
    )

    # 2. teskilat_semasi.docx — it's an IMAGE, has no text in the file itself.
    # Hierarchy converted to text (read from the diagram):
    teskilat_text = """TİKA Teşkilat Şeması

T.C. Kültür ve Turizm Bakanı, TİKA'nın bağlı olduğu üst makamdır.
T.C. Kültür ve Turizm Bakan Yardımcısı, Bakan'a bağlı olarak görev yapar.
TİKA Başkanı, Bakan Yardımcısı'na bağlı olarak görev yapar.

TİKA Başkanı'na doğrudan bağlı danışma ve destek birimleri:
- Hukuk Müşavirliği
- İç Denetçiler
- Özel Kalem Müdürlüğü
- Personel ve Destek Hizmetleri Dairesi Başkanlığı
- Başkan Yardımcısı
- Başkan Yardımcısı
- Başkan Yardımcısı

TİKA Başkanı'na bağlı 3 Başkan Yardımcısı ve onlara bağlı daire başkanlıkları:

1. Başkan Yardımcısı - bağlı daireler:
   - Strateji Geliştirme Dairesi Başkanlığı
   - Orta Asya ve Kafkaslar Dairesi Başkanlığı

2. Başkan Yardımcısı - bağlı daireler:
   - Balkanlar ve Doğu Avrupa Dairesi Başkanlığı
   - Orta Doğu ve Afrika Dairesi Başkanlığı

3. Başkan Yardımcısı - bağlı daireler:
   - Dış İlişkiler ve Ortaklıklar Dairesi Başkanlığı
   - Doğu ve Güney Asya, Pasifik ve Latin Amerika Dairesi Başkanlığı

Program Koordinasyon Ofisleri, bu yapının altında, sahada faaliyet gösteren birimlerdir."""

    ingest_local_document(
        title="TİKA Teşkilat Şeması - TİKA (İç Doküman)",
        text=teskilat_text,
        doc_path="internal:teskilat-semasi-docx"
    )