from app.db.connection import get_connection
from app.ingestion.chunking import chunk_text
from app.embeddings.embedder import embed_texts
import time

def process_document(document_id: str, full_text: str, source_url: str = None):
    chunks = chunk_text(full_text)
    conn = get_connection()
    cur = conn.cursor()

    BATCH_SIZE = 20
    for start in range(0, len(chunks), BATCH_SIZE):
        batch = chunks[start:start + BATCH_SIZE]
        embeddings = embed_texts(batch)
        for offset, (chunk, embedding) in enumerate(zip(batch, embeddings)):
            idx = start + offset
            cur.execute(
                "INSERT INTO chunks (document_id, chunk_index, chunk_text, embedding, source_url) VALUES (%s, %s, %s, %s, %s)",
                (document_id, idx, chunk, embedding, source_url)
            )
        time.sleep(5)

    cur.execute("UPDATE documents SET doc_status = 'ready' WHERE id = %s", (document_id,))
    conn.commit()
    cur.close()
    conn.close()