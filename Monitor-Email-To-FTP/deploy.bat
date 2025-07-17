@echo off
REM Script para configurar e fazer deploy da aplicação no Windows
REM Use: deploy.bat [development|staging|production]

set ENVIRONMENT=%1
if "%ENVIRONMENT%"=="" set ENVIRONMENT=development

echo Configurando ambiente: %ENVIRONMENT%

REM Definir URLs baseadas no ambiente
if "%ENVIRONMENT%"=="development" (
    set API_URL=http://localhost:8080/api/v1/email-to-ftp
) else if "%ENVIRONMENT%"=="staging" (
    set API_URL=https://staging-api.exemplo.com/api/v1/email-to-ftp
) else if "%ENVIRONMENT%"=="production" (
    set API_URL=https://172.20.21.8:11000/api/v1/email-to-ftp
) else (
    echo Ambiente inválido. Use: development, staging ou production
    exit /b 1
)

REM Criar arquivo de configuração dinâmico
(
echo // Configurações da aplicação - Gerado automaticamente
echo window.API_CONFIG = {
echo     baseUrl: '%API_URL%',
echo     environment: '%ENVIRONMENT%',
echo     refreshInterval: 30000,
echo     connectionTestInterval: 120000,
echo     schedulingUpdateInterval: 10000
echo };
echo.
echo console.log('Configuração carregada para ambiente:', window.API_CONFIG.environment^);
echo console.log('URL da API:', window.API_CONFIG.baseUrl^);
) > config.js

echo Configuração criada com sucesso!
echo Ambiente: %ENVIRONMENT%
echo URL da API: %API_URL%
echo.
echo Para usar em outros ambientes:
echo   deploy.bat development
echo   deploy.bat staging
echo   deploy.bat production
