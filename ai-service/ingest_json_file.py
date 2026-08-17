from dotenv import load_dotenv
load_dotenv()

import json
import uuid
import time
import sys

# Forsiranje UTF-8 kodiranja za terminal
sys.stdout.reconfigure(encoding='utf-8')

from app.ingestion.pipeline import process_document
from app.db.connection import get_connection

def ingest_json_file(filepath: str, doc_type: str = "web"):
    with open(filepath, "r", encoding="utf-8") as f:
        records = json.load(f)

    for record in records:
        title = record.get("title", "Untitled")
        source_url = record.get("url")
        main_text = record.get("content", "")

        # 1. Spašavanje glavnog teksta sa web stranice
        if main_text.strip():
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
            time.sleep(5) # Pauza za Gemini API

        # 2. Spašavanje tekstova iz pripadajućih PDF-ova
        pdf_contents = record.get("pdf_contents", [])
        for pdf in pdf_contents:
            pdf_text = pdf.get("text", "")
            pdf_url = pdf.get("pdf_url", "")

            if pdf_text.strip():
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
                time.sleep(5) # Pauza za Gemini API

if __name__ == "__main__":
    ingest_json_file(r"C:\Users\Korisnik\chatbot_tika\data_scraper\tika_data_processed.json")