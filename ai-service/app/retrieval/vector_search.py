from app.db.connection import get_connection
from app.embeddings.embedder import embed_text

def search_similar_chunks(question: str, top_k: int = 5):
    query_embedding = embed_text(question, task_type="RETRIEVAL_QUERY")
    conn = get_connection()
    cur = conn.cursor()

    cur.execute(
        """
        SELECT c.id, c.chunk_text, c.document_id, c.source_page,
               1 - (c.embedding <=> %s::vector) AS similarity
        FROM chunks c
        ORDER BY c.embedding <=> %s::vector
        LIMIT %s
        """,
        (query_embedding, query_embedding, top_k)
    )

    results = cur.fetchall()
    cur.close()
    conn.close()
    return results