import json
import re
from urllib.parse import urljoin, urlparse
import requests
from bs4 import BeautifulSoup

BASE_URL = 'https://tika.gov.tr'
HEADERS = {
    'User-Agent': (
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML,'
        ' like Gecko) Chrome/120.0.0.0 Safari/537.36'
    )
}


def clean_text(text):
  if not text:
    return ''
  return re.sub(r'\s+', ' ', text).strip()


def is_valid_tika_url(url):
  """Filters only valid web pages belonging to the tika.gov.tr domain."""
  parsed = urlparse(url)
  # Skip social media, file download links, and media extensions
  ignored_extensions = (
      '.jpg',
      '.jpeg',
      '.png',
      '.gif',
      '.pdf',
      '.zip',
      '.rar',
      '.docx',
  )
  if any(parsed.path.lower().endswith(ext) for ext in ignored_extensions):
    return False
  return 'tika.gov.tr' in parsed.netloc and parsed.scheme in ('http', 'https')


def scrape_page(url):
  try:
    response = requests.get(url, headers=HEADERS, timeout=15)
    response.raise_for_status()
    response.encoding = 'utf-8'

    soup = BeautifulSoup(response.text, 'html.parser')

    # Get title
    title = ''
    if soup.find('h1'):
      title = soup.find('h1').get_text()
    elif soup.find('title'):
      title = soup.find('title').get_text()

    # Get paragraphs and content text
    paragraphs = [p.get_text() for p in soup.find_all(['p', 'article', 'li'])]
    content = clean_text(' '.join(paragraphs))

    # Collect PDF links on the page
    pdf_urls = []
    # Collect all other subpage links on the page
    internal_links = set()

    for a_tag in soup.find_all('a', href=True):
      href = a_tag['href'].strip()
      full_url = urljoin(url, href)

      if href.lower().endswith('.pdf'):
        if full_url not in pdf_urls:
          pdf_urls.append(full_url)
      elif is_valid_tika_url(full_url):
        # Remove in-page anchors (#)
        clean_url = full_url.split('#')[0].rstrip('/')
        if clean_url:
          internal_links.add(clean_url)

    return {
        'url': url,
        'title': clean_text(title),
        'content': content,
        'pdf_urls': pdf_urls,
        'discovered_links': list(internal_links),
    }

  except Exception as e:
    print(f'   [!] Hata ({url}): {e}')
    return None


def run_full_crawler():
  print('=== TİKA TÜM SİTE TARAMA BAŞLADI ===\n')

  visited_urls = set()
  to_visit = ['https://tika.gov.tr/']
  scraped_results = []

  count = 0

  while to_visit:
    current_url = to_visit.pop(0)

    # Düzgün url karşılaştırması için temizle
    clean_current = current_url.rstrip('/')
    if clean_current in visited_urls:
      continue

    visited_urls.add(clean_current)
    count += 1

    print(
        f'[{count}] Taranıyor: {current_url} | (Kuyrukta bekleyen:'
        f' {len(to_visit)})'
    )

    page_data = scrape_page(current_url)

    if page_data:
      # Eğer anlamlı içerik varsa listeye ekle
      if page_data['content'] or page_data['pdf_urls']:
        scraped_results.append({
            'url': page_data['url'],
            'title': page_data['title'],
            'content': page_data['content'],
            'pdf_urls': page_data['pdf_urls'],
        })

      # Yeni bulunan linkleri kuyruğa ekle
      for link in page_data['discovered_links']:
        if link.rstrip('/') not in visited_urls and link not in to_visit:
          to_visit.append(link)

  # Tüm verileri JSON dosyasına kaydet
  output_filename = 'tika_data.json'
  with open(output_filename, 'w', encoding='utf-8') as f:
    json.dump(scraped_results, f, ensure_ascii=False, indent=4)

  print('\n=== TARAMA TAMAMLANDI ===')
  print(f'Toplam taranan sayfa sayısı: {len(visited_urls)}')
  print(f'Verisi çekilen başarılı sayfa sayısı: {len(scraped_results)}')
  print(f'Çıktı dosyası: {output_filename}')


if __name__ == '__main__':
  run_full_crawler()