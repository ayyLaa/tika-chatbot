from fastapi import FastAPI, Header, HTTPException, Depends
import os
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
from app.ingestion.web_scraper import scrape_page, crawl_site

INTERNAL_API_KEY = os.getenv("INTERNAL_API_KEY")

def verify_internal_key(x_internal_key: str = Header(None)):
    if x_internal_key != INTERNAL_API_KEY:
        raise HTTPException(status_code=403, detail="Forbidden")
app = FastAPI()

@app.post("/query", response_model=QueryResponse, dependencies=[Depends(verify_internal_key)])
def query(request: QueryRequest):
    start = time.time()
    results = search_similar_chunks(request.question, request.top_k)

    relevant_results = [r for r in results if r[4] > 0.6]

    if not relevant_results:
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

@app.post("/ingest", dependencies=[Depends(verify_internal_key)])
def ingest(request: IngestRequest, full_text: str):
    process_document(request.document_id, request.full_text)
    return {"status": "processed"}

@app.post("/ingest-web", dependencies=[Depends(verify_internal_key)])
def ingest_web(url: str):
    page = scrape_page(url)
    document_id = str(uuid.uuid4())

    conn = get_connection()
    cur = conn.cursor()
    cur.execute(
        "INSERT INTO documents (id, file_name, doc_type, doc_path, doc_status) VALUES (%s, %s, %s, %s, %s)",
        (document_id, page["title"], "web", url, "processing")
    )
    conn.commit()
    cur.close()
    conn.close()

    process_document(document_id, page["text"], source_url=url)
    return {"status": "ok", "url": url, "document_id": document_id}

@app.post("/ingest-web-crawl", dependencies=[Depends(verify_internal_key)])
def ingest_web_crawl(start_url: str, max_pages: int = 30, max_depth: int = 2):
    pages = crawl_site(start_url, max_pages=max_pages, max_depth=max_depth, path_prefix=start_url)

    processed = 0
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
        processed += 1

    return {"status": "ok", "pages_processed": processed}

