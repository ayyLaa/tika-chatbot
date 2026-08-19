import os, time
from concurrent.futures import ThreadPoolExecutor, TimeoutError as FutureTimeoutError
from dotenv import load_dotenv
load_dotenv()
from google import genai

client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))
_executor = ThreadPoolExecutor(max_workers=8)


def _call_with_timeout(fn, timeout_seconds: int):
    future = _executor.submit(fn)
    try:
        return future.result(timeout=timeout_seconds)
    except FutureTimeoutError:
        raise Exception(f"Gemini poziv nije odgovorio u roku od {timeout_seconds}s.")


def generate_answer(prompt: str) -> str:
    try:
        response = _call_with_timeout(
            lambda: client.models.generate_content(model='gemini-2.5-flash', contents=prompt),
            25
        )
        return response.text
    except Exception as e:
        raise Exception(f"Greška pri komunikaciji sa Gemini API: {str(e)}")


def generate_answer_with_usage(prompt: str, max_retries: int = 2, call_timeout: int = 25) -> tuple[str, int | None]:
    for attempt in range(max_retries):
        try:
            response = _call_with_timeout(
                lambda: client.models.generate_content(model='gemini-2.5-flash', contents=prompt),
                call_timeout
            )
            tokens = response.usage_metadata.total_token_count if response.usage_metadata else None
            return response.text, tokens
        except Exception as e:
            is_rate_limit = "429" in str(e) or "RESOURCE_EXHAUSTED" in str(e)
            if attempt < max_retries - 1:
                wait = 30 if is_rate_limit else 2 ** attempt
                time.sleep(wait)
                continue
            raise Exception(f"Gemini greška nakon {max_retries} pokušaja: {str(e)}")