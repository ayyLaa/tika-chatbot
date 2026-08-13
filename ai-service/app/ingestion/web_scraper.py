import requests
from bs4 import BeautifulSoup
from urllib.parse import urlparse

ALLOWED_DOMAINS = {"gov.tr"}

def is_allowed_domain(url: str) -> bool:
    domain = urlparse(url).netloc
    return any(domain.endswith(allowed) for allowed in ALLOWED_DOMAINS)

def scrape_page(url: str) -> dict:
    if not is_allowed_domain(url):
        raise ValueError(f"The domain is not allowed: {url}")

    response = requests.get(url, timeout=10, headers={"User-Agent": "TikaInternalBot/1.0"})
    response.raise_for_status()
    soup = BeautifulSoup(response.content, "lxml")

    for tag in soup(["script", "style", "nav", "footer", "header"]):
        tag.decompose()

    title = soup.title.string if soup.title else url
    text = soup.get_text(separator="\n", strip=True)
    return {"url": url, "title": title, "text": text}