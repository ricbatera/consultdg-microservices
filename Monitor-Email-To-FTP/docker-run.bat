@echo off
REM Script para construir e executar o container Docker no Windows
REM Uso: docker-run.bat [API_URL] [PORTA]

set API_URL=%1
set PORTA=%2
set CONTAINER_NAME=monitor-email-to-ftp

if "%API_URL%"=="" set API_URL=http://localhost:8080/api/v1/email-to-ftp
if "%PORTA%"=="" set PORTA=8081

echo === Construindo e executando Monitor Email to FTP ===
echo API URL: %API_URL%
echo Porta: %PORTA%
echo Container: %CONTAINER_NAME%
echo ======================================================

REM Parar e remover container existente se houver
for /f %%i in ('docker ps -q -f name=%CONTAINER_NAME% 2^>nul') do (
    echo Parando container existente...
    docker stop %CONTAINER_NAME%
)

for /f %%i in ('docker ps -aq -f name=%CONTAINER_NAME% 2^>nul') do (
    echo Removendo container existente...
    docker rm %CONTAINER_NAME%
)

REM Construir imagem
echo Construindo imagem Docker...
docker build -t %CONTAINER_NAME% .

if %errorlevel% neq 0 (
    echo ERRO: Falha ao construir a imagem Docker
    pause
    exit /b 1
)

REM Executar container
echo Iniciando container...
docker run -d ^
  --name %CONTAINER_NAME% ^
  -p %PORTA%:80 ^
  -e API_BASE_URL="%API_URL%" ^
  -e ENVIRONMENT="production" ^
  -e REFRESH_INTERVAL=30000 ^
  -e CONNECTION_TEST_INTERVAL=120000 ^
  -e SCHEDULING_UPDATE_INTERVAL=10000 ^
  -v /home/ricardo/microservicos/consultdg-microservices/Monitor-Email-To-FTP/logs:/var/log/nginx ^
  %CONTAINER_NAME%

if %errorlevel% equ 0 (
    echo.
    echo ✅ Container iniciado com sucesso!
    echo 🌐 Acesse: http://localhost:%PORTA%
    echo 📊 Para ver logs: docker logs %CONTAINER_NAME%
    echo 🛑 Para parar: docker stop %CONTAINER_NAME%
    echo.
    echo === Status do Container ===
    docker ps | findstr %CONTAINER_NAME%
) else (
    echo ❌ ERRO: Falha ao iniciar o container
    pause
    exit /b 1
)

pause
