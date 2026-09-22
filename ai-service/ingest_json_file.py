from dotenv import load_dotenv
load_dotenv()

from tqdm import tqdm

import json
import uuid
import time
import sys

import os
os.environ["GEMINI_API_KEY"] = os.getenv("GEMINI_API_KEY_INGEST_JSON")
# Force UTF-8 encoding for terminal
sys.stdout.reconfigure(encoding='utf-8')

from app.ingestion.pipeline import process_document
from app.db.connection import get_connection

def document_already_exists(source_url: str) -> bool:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT 1 FROM documents WHERE doc_path = %s AND doc_status = 'ready'", (source_url,))
    exists = cur.fetchone() is not None
    cur.close()
    conn.close()
    return exists

def ingest_json_file(filepath: str, doc_type: str = "web"):
    with open(filepath, "r", encoding="utf-8") as f:
        records = json.load(f)

    for record in tqdm(records, desc="Filling database", unit="doc"):
        title = record.get("title", "Untitled")
        source_url = record.get("url")
        main_text = record.get("content", "")

        # 1. Save main text from the web page
        if main_text.strip() and not document_already_exists(source_url or "json_import"):
            document_id = str(uuid.uuid4())
            conn = get_connection()
            cur = conn.cursor()
            cur.execute(
                "INSERT INTO documents (id, file_name, doc_type, doc_path, doc_status) VALUES (%s, %s, %s, %s, %s)",
                (document_id, title, doc_type, source_url or "json_import", "processing")
            )
            conn.commit()
            cur.close()
            conn.close()

            process_document(document_id, main_text, source_url=source_url)
            print(f"OK (Web): {title}")
            time.sleep(10) # Pause for Gemini API
        elif main_text.strip():
            print(f"SKIPPED (already exists): {title}")

        # 2. Save texts from associated PDFs
        pdf_contents = record.get("pdf_contents", [])
        for pdf in pdf_contents:
            pdf_text = pdf.get("text", "")
            pdf_url = pdf.get("pdf_url", "")

            if pdf_text.strip() and not document_already_exists(pdf_url or "json_import"):
                pdf_id = str(uuid.uuid4())
                pdf_title = f"{title} - [PDF]"

                conn = get_connection()
                cur = conn.cursor()
                cur.execute(
                    "INSERT INTO documents (id, file_name, doc_type, doc_path, doc_status) VALUES (%s, %s, %s, %s, %s)",
                    (pdf_id, pdf_title, "pdf", pdf_url, "processing")
                )
                conn.commit()
                cur.close()
                conn.close()

                process_document(pdf_id, pdf_text, source_url=pdf_url)
                print(f"OK (PDF): {pdf_url}")
                time.sleep(2)
            elif pdf_text.strip():
                print(f"SKIPPED (already exists): {pdf_url}")


if __name__ == "__main__":
    ingest_json_file(r"C:\Users\Korisnik\chatbot_tika\data_scraper\tika_data_short.json")