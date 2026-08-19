import os, time
from concurrent.futures import ThreadPoolExecutor, TimeoutError as FutureTimeoutError
from dotenv import load_dotenv
load_dotenv()
from google import genai
from google.genai import types

client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))
_executor = ThreadPoolExecutor(max_workers=8)

EMBED_MODEL = "gemini-embedding-001"
EMBED_DIM = 768


def _call_with_timeout(fn, timeout_seconds: int):
    future = _executor.submit(fn)
    try:
        return future.result(timeout=timeout_seconds)
    except FutureTimeoutError:
        raise Exception(f"Gemini poziv nije odgovorio u roku od {timeout_seconds}s.")


def embed_text(text: str, task_type: str = "RETRIEVAL_QUERY", max_retries: int = 2, call_timeout: int = 10) -> list[float]:
    for attempt in range(max_retries):
        try:
            response = _call_with_timeout(
                lambda: client.models.embed_content(
                    model=EMBED_MODEL, contents=text,
                    config=types.EmbedContentConfig(task_type=task_type, output_dimensionality=EMBED_DIM),
                ),
                call_timeout
            )
            return response.embeddings[0].values
        except Exception:
            if attempt < max_retries - 1:
                time.sleep(2 ** attempt)
                continue
            raise Exception("Nije moguće generisati vektor preko Gemini API-ja.")


def embed_texts(texts: list[str], task_type: str = "RETRIEVAL_DOCUMENT", max_retries: int = 4, call_timeout: int = 15) -> list[list[float]]:
    for attempt in range(max_retries):
        try:
            response = _call_with_timeout(
                lambda: client.models.embed_content(
                    model=EMBED_MODEL, contents=texts,
                    config=types.EmbedContentConfig(task_type=task_type, output_dimensionality=EMBED_DIM),
                ),
                call_timeout
            )
            if len(response.embeddings) != len(texts):
                raise RuntimeError(f"Batch embed vratio {len(response.embeddings)} vektora za {len(texts)} tekstova.")
            return [e.values for e in response.embeddings]
        except Exception as e:
            is_rate_limit = "429" in str(e) or "RESOURCE_EXHAUSTED" in str(e)
            if attempt < max_retries - 1:
                wait = 30 if is_rate_limit else 2 ** attempt
                time.sleep(wait)
                continue
            raise