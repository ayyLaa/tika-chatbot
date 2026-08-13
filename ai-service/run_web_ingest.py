from dotenv import load_dotenv
load_dotenv()

from app.ingestion.web_scraper import crawl_site
from app.ingestion.pipeline import process_document
from app.db.connection import get_connection
import uuid

SITES_TO_INGEST = [
    {"url": "https://tika.gov.tr/faaliyetlerimiz/", "max_pages": 15, "max_depth": 1},
    # dodaj još sajtova ovdje kad odlučiš koji
]

def ingest_site(config):
    pages = crawl_site(
        config["url"], max_pages=config["max_pages"],
        max_depth=config["max_depth"], path_prefix=config["url"]
    )
    for page in pages:
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