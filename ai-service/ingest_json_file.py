from dotenv import load_dotenv
load_dotenv()

import json
import uuid
from app.ingestion.pipeline import process_document
from app.db.connection import get_connection

def ingest_json_file(filepath: str, doc_type: str = "web"):
    with open(filepath, "r", encoding="utf-8") as f:
        records = json.load(f)

    for record in records:
        title = record.get("title", "Untitled")
        text = record.get("text", "")
        source_url = record.get("url")  # ako postoji

        if not text.strip():
            continue

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

        process_document(document_id, text, source_url=source_url)
        print(f"✓ {title}")

if __name__ == "__main__":
    ingest_json_file("data/tika_data_processed.json")