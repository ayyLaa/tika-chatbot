from pydantic import BaseModel
from typing import Optional

class IngestRequest(BaseModel):
    document_id: str
    full_text: str

class ConversationTurn(BaseModel):
    question: str
    answer: str

class QueryRequest(BaseModel):
    session_id: str
    question: str
    top_k: int = 5
    conversation_history: list[ConversationTurn] = []

class SourceChunk(BaseModel):
    document: str
    page: Optional[int]
    chunk_id: str

class QueryResponse(BaseModel):
    answer: str
    sources: list[SourceChunk]
    response_time_ms: int
    tokens_used: Optional[int] = None