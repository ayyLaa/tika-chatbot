from dotenv import load_dotenv
load_dotenv()

from google import genai
import os

client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))

def generate_answer(prompt: str) -> str:
    response = client.models.generate_content(
        model="gemini-2.5-flash",
        contents=prompt,
        config={"temperature": 0.1}
    )
    return response.text

import time

def generate_answer_with_usage(prompt: str, max_retries: int = 3) -> tuple[str, int | None]:
    for attempt in range(max_retries):
        try:
            response = client.models.generate_content(
                model="gemini-flash-latest",
                contents=prompt,
                config={"temperature": 0.1}
            )
            usage = getattr(response, "usage_metadata", None)
            total_tokens = usage.total_token_count if usage else None
            return response.text, total_tokens
        except Exception as e:
            if attempt < max_retries - 1:
                time.sleep(2 ** attempt)  # 1s, 2s, 4s
                continue
            raise
    tokens = getattr(response, "usage_metadata", None)
    total_tokens = tokens.total_token_count if tokens else None
    return response.text, total_tokens