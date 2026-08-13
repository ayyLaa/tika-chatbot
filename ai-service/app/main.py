from fastapi import FastAPI

from app.db.connection import get_connection
from app.models.schemas import QueryRequest, QueryResponse, SourceChunk
from app.retrieval.vector_search import search_similar_chunks
from app.generation.prompt_templates import build_prompt
from app.generation.gemini_client import generate_answer, generate_answer_with_usage
from app.ingestion.pipeline import process_document
from app.models.schemas import IngestRequest
from app.ingestion.web_scraper import scrape_page
import uuid
import time
app = FastAPI()

@app.post("/query", response_model=QueryResponse)
def query(request: QueryRequest):
    start = time.time()
    results = search_similar_chunks(request.question, request.top_k)

    if not results:
        elapsed = int((time.time() - start) * 1000)
        return QueryResponse(
            answer="I don't have enough information in the available documents to answer this question.",
            sources=[],
            response_time_ms=elapsed
        )

    context_chunks = [r[1] for r in results]
    prompt = build_prompt(request.question, context_chunks, history=request.conversation_history)
    answer, tokens = generate_answer_with_usage(prompt)

    sources = [SourceChunk(document=str(r[2]), page=r[3], chunk_id=str(r[0])) for r in results]
    elapsed = int((time.time() - start) * 1000)

    return QueryResponse(answer=answer, sources=sources, response_time_ms=elapsed, tokens_used=tokens)

@app.post("/ingest")
def ingest(request: IngestRequest, full_text: str):
    process_document(request.document_id, request.full_text)
    return {"status": "processed"}

@app.post("/ingest-web")
def ingest_web(url: str):
    page = scrape_page(url)
    document_id = str(uuid.uuid4())

    conn = get_connection()
    cur = conn.cursor()
    cur.execute(
        "INSERT INTO documents (id, file_name, doc_type, doc_path, doc_status) VALUES (%s, %s, %s, %s, %s)",
        (document_id, page["title"], "web", url, "ready")
    )
    conn.commit()
    cur.close()
    conn.close()

    process_document(document_id, page["text"], source_url=url)
    return {"status": "ok", "url": url, "document_id": document_id}

