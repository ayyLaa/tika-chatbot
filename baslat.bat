@echo off
title TIKA AI Servisleri Baslatici
echo ==========================================
echo       TIKA AI SISTEMI BASLATILIYOR
echo ==========================================

echo.
echo [1/4] Veritabani baslatiliyor (Docker)...
docker-compose up -d

echo.
echo [2/4] Python AI Servisi baslatiliyor...
start powershell -NoExit -Command "cd ai-service; uvicorn app.main:app --reload --port 8000"

echo.
echo [3/4] Java Spring Boot baslatiliyor...
start powershell -NoExit -Command "cd backend\chatbot; Get-Content .env | Foreach-Object { if ($_ -match '^([^=]+)=(.*)$') { [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2]) } }; .\mvnw spring-boot:run"

echo.
echo [4/4] Vue Frontend baslatiliyor...
start powershell -NoExit -Command "cd tika-frontend; npm run dev"

echo.
echo ==========================================
echo TUM SERVISLER BASARIYLA AYAGA KALDIRILDI!
echo ==========================================
echo Frontend: http://localhost:5173
echo AI API: http://localhost:8000
echo Java API: http://localhost:8080
echo Bu siyah ekrani kapatabilirsiniz, servisler ayri pencerelerde calismaya devam edecektir.
pauses