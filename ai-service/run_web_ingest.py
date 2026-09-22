import sys
sys.stdout.reconfigure(encoding='utf-8')

from dotenv import load_dotenv
load_dotenv()

import os
os.environ["GEMINI_API_KEY"] = os.getenv("GEMINI_API_KEY_INGEST_WEB")

from app.ingestion.web_scraper import crawl_site
from app.ingestion.pipeline import process_document
from app.db.connection import get_connection
import uuid

SITES_TO_INGEST = [
    {"url": "https://tika.gov.tr/faaliyetlerimiz/", "max_pages": 15, "max_depth": 1},
    {"url": "https://www.ktb.gov.tr/TR-96130/kurulus-ve-gorevler.html", "max_pages": 1, "max_depth": 0},
    {"url": "https://www.ktb.gov.tr/TR-96132/teskilat-semasi.html", "max_pages": 1, "max_depth": 0},
    {"url": "https://www.ktb.gov.tr/TR-96699/merkez-teskilati.html", "max_pages": 1, "max_depth": 0},
    {"url": "https://www.ktb.gov.tr/TR-96283/kulturel-miras.html", "max_pages": 1, "max_depth": 0},
    {"url": "https://www.ktb.gov.tr/TR-134106/somut-kulturel-miras.html", "max_pages": 1, "max_depth": 0},
    {"url": "https://www.ktb.gov.tr/", "max_pages": 20, "max_depth": 1},
]

def document_already_exists(source_url: str) -> bool:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT 1 FROM documents WHERE doc_path = %s AND doc_status = 'ready'", (source_url,))
    exists = cur.fetchone() is not None
    cur.close()
    conn.close()
    return exists

def ingest_site(config):
    pages = crawl_site(
        config["url"], max_pages=config["max_pages"],
        max_depth=config["max_depth"], path_prefix=config["url"]
    )
    for page in pages:
        if document_already_exists(page["url"]):
            print(f"SKIPPED (already exists): {page['url']}")
            continue

        document_id = str(uuid.uuid4())
        conn = get_connection()
        cur = conn.cursor()
        cur.execute(
            "INSERT INTO documents (id, file_name, doc_type, doc_path, doc_status) VALUES (%s, %s, %s, %s, %s)",
            (document_id, page["title"], "web", page["url"], "processing")
        )
        conn.commit()
        cur.close()
        conn.close()
        process_document(document_id, page["text"], source_url=page["url"])
        print(f"✓ {page['title']} — {page['url']}")

if __name__ == "__main__":
    for site in SITES_TO_INGEST:
        ingest_site(site)