from app.db.connection import get_connection
from app.ingestion.chunking import chunk_text
from app.embeddings.embedder import embed_text

def process_document(document_id: str, full_text: str, source_url: str = None):
    chunks = chunk_text(full_text)
    conn = get_connection()
    cur = conn.cursor()

    for idx, chunk in enumerate(chunks):
        embedding = embed_text(chunk)
        cur.execute(
            """
            INSERT INTO chunks (document_id, chunk_index, chunk_text, embedding, source_url)
            VALUES (%s, %s, %s, %s, %s)
            """,
            (document_id, idx, chunk, embedding, source_url)
        )

    conn.commit()
    cur.close()
    conn.close()