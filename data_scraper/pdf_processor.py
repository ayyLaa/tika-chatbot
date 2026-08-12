import json
import os
import re
import pdfplumber
import requests

PDF_DIR = "downloaded_pdfs"
os.makedirs(PDF_DIR, exist_ok=True)


def clean_text(text):
  if not text:
    return ""
  text = re.sub(r"\s+", " ", text)
  return text.strip()


def download_pdf(pdf_url):
  try:
    filename = pdf_url.split("/")[-1]
    if not filename.lower().endswith(".pdf"):
      filename = "document.pdf"

    filename = re.sub(r'[\\/*?:"<>|]', "_", filename)
    filepath = os.path.join(PDF_DIR, filename)

    if os.path.exists(filepath):
      return filepath

    headers = {
        "User-Agent": (
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
        )
    }
    response = requests.get(pdf_url, headers=headers, timeout=20)
    response.raise_for_status()

    with open(filepath, "wb") as f:
      f.write(response.content)

    return filepath
  except Exception as e:
    print(f"   [!] PDF indirilemedi ({pdf_url}): {e}")
    return None


def extract_pdf_content(pdf_path):
  full_text = ""
  try:
    with pdfplumber.open(pdf_path) as pdf:
      for page in pdf.pages:
        text = page.extract_text()
        if text:
          full_text += text + " "
  except Exception as e:
    print(f"   [!] PDF okuma hatası ({pdf_path}): {e}")

  return clean_text(full_text)


def process_all_data():
  json_file = "tika_data.json"

  if not os.path.exists(json_file):
    print("HATA: tika_data.json dosyası bulunamadı!")
    return

  print("1. tika_data.json okunuyor...")
  with open(json_file, "r", encoding="utf-8") as f:
    data_list = json.load(f)

  print("2. Sayfalardaki PDF dosyaları taranıyor ve metinler çıkarılıyor...\n")

  total_pdfs_processed = 0

  for index, item in enumerate(data_list):
    pdf_urls = item.get("pdf_urls", [])
    item["pdf_contents"] = []

    if pdf_urls:
      print(f"[{index+1}/{len(data_list)}] Sayfada {len(pdf_urls)} PDF var.")

    for pdf_url in pdf_urls:
      print(f"   -> İndiriliyor/İşleniyor: {pdf_url}")
      filepath = download_pdf(pdf_url)

      if filepath:
        pdf_text = extract_pdf_content(filepath)
        if pdf_text:
          item["pdf_contents"].append({"pdf_url": pdf_url, "text": pdf_text})
          total_pdfs_processed += 1

  output_file = "tika_data_processed.json"
  with open(output_file, "w", encoding="utf-8") as f:
    json.dump(data_list, f, ensure_ascii=False, indent=4)

  print("\n=== TÜM VERİ TEMİZLEME VE İŞLEME TAMAMLANDI ===")
  print(f"Toplam işlenen PDF sayısı: {total_pdfs_processed}")
  print(f"Backend & AI için nihai veri dosyası: {output_file}")


if __name__ == "__main__":
  process_all_data()
  