from dotenv import load_dotenv
load_dotenv()

from google import genai
import os
import time

client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))

def embed_text(text: str, max_retries: int = 3) -> list[float]:
    for attempt in range(max_retries):
        try:
            result = client.models.embed_content(
                model="gemini-embedding-001",
                contents=text,
                config={"output_dimensionality": 768}
            )
            return result.embeddings[0].values
        except Exception:
            if attempt < max_retries - 1:
                time.sleep(2 ** attempt)
                continue
            raise