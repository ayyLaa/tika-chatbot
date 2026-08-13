import requests
from bs4 import BeautifulSoup
from urllib.parse import urlparse
from collections import deque
from urllib.parse import urljoin

ALLOWED_DOMAINS = {"gov.tr", "edu.tr"}

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

def crawl_site(start_url: str, max_pages: int = 30, max_depth: int = 2, path_prefix: str = None) -> list[dict]:
    if not is_allowed_domain(start_url):
        raise ValueError(f"The domain is not allowed: {start_url}")

    visited = set()
    queue = deque([(start_url, 0)])
    results = []
    base_domain = urlparse(start_url).netloc

    while queue and len(visited) < max_pages:
        url, depth = queue.popleft()
        if url in visited or depth > max_depth:
            continue
        if path_prefix and not url.startswith(path_prefix):
            continue

        try:
            response = requests.get(url, timeout=10, headers={"User-Agent": "TikaInternalBot/1.0"})
            response.raise_for_status()
        except requests.RequestException:
            continue

        visited.add(url)
        soup = BeautifulSoup(response.content, "lxml")
        for tag in soup(["script", "style", "nav", "footer", "header"]):
            tag.decompose()

        title = soup.title.string if soup.title else url
        text = soup.get_text(separator="\n", strip=True)
        results.append({"url": url, "title": title, "text": text})

        for link in soup.find_all("a", href=True):
            absolute_url = urljoin(url, link["href"])
            if urlparse(absolute_url).netloc == base_domain and absolute_url not in visited:
                queue.append((absolute_url, depth + 1))

    return results