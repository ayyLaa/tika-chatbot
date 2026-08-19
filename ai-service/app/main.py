import time
from fastapi import FastAPI, HTTPException, Header, Depends
import os
from dotenv import load_dotenv
load_dotenv()

from app.retrieval.vector_search import search_similar_chunks
from app.generation.gemini_client import generate_answer_with_usage
from app.generation.prompt_templates import build_prompt
from app.models.schemas import QueryRequest, QueryResponse, SourceChunk

app = FastAPI(title="TİKA AI Service", version="1.0.0")

INTERNAL_API_KEY = os.getenv("INTERNAL_API_KEY")


def verify_internal_key(x_internal_key: str = Header(None)):
    if x_internal_key != INTERNAL_API_KEY:
        raise HTTPException(status_code=403, detail="Forbidden")

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/query", response_model=QueryResponse, dependencies=[Depends(verify_internal_key)])
def query(request: QueryRequest):
    start = time.time()
    results = search_similar_chunks(request.question, request.top_k)

    # similarity threshold filter (dio 9 ispod)
    relevant_results = [r for r in results if r[4] > 0.6]

    if not relevant_results:
        elapsed = int((time.time() - start) * 1000)
        return QueryResponse(
            answer="I don't have enough information in the available documents to answer this question.",
            sources=[],
            response_time_ms=elapsed
        )

    context_chunks = [r[1] for r in relevant_results]
    prompt = build_prompt(request.question, context_chunks, history=request.conversation_history)
    answer, tokens = generate_answer_with_usage(prompt)

    sources = [SourceChunk(document=str(r[2]), page=r[3], chunk_id=str(r[0])) for r in relevant_results]
    elapsed = int((time.time() - start) * 1000)

    return QueryResponse(answer=answer, sources=sources, response_time_ms=elapsed, tokens_used=tokens)