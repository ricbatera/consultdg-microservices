#!/bin/sh

# Script de entrada para substituir variáveis de ambiente no config.js

# Definir valores padrão se as variáveis não estiverem definidas
export API_BASE_URL=${API_BASE_URL:-"http://localhost:8080/api/v1/email-to-ftp"}
export ENVIRONMENT=${ENVIRONMENT:-"production"}
export REFRESH_INTERVAL=${REFRESH_INTERVAL:-30000}
export CONNECTION_TEST_INTERVAL=${CONNECTION_TEST_INTERVAL:-120000}
export SCHEDULING_UPDATE_INTERVAL=${SCHEDULING_UPDATE_INTERVAL:-10000}

echo "=== Configurando aplicação ==="
echo "API_BASE_URL: $API_BASE_URL"
echo "ENVIRONMENT: $ENVIRONMENT"
echo "REFRESH_INTERVAL: $REFRESH_INTERVAL"
echo "CONNECTION_TEST_INTERVAL: $CONNECTION_TEST_INTERVAL"
echo "SCHEDULING_UPDATE_INTERVAL: $SCHEDULING_UPDATE_INTERVAL"
echo "=============================="

# Substituir variáveis de ambiente no template e criar o config.js final
envsubst '${API_BASE_URL},${ENVIRONMENT},${REFRESH_INTERVAL},${CONNECTION_TEST_INTERVAL},${SCHEDULING_UPDATE_INTERVAL}' \
    < /usr/share/nginx/html/config.template.js \
    > /usr/share/nginx/html/config.js

echo "Arquivo config.js gerado com sucesso!"

# Verificar se o arquivo foi criado corretamente
if [ -f "/usr/share/nginx/html/config.js" ]; then
    echo "Conteúdo do config.js:"
    cat /usr/share/nginx/html/config.js
else
    echo "ERRO: Falha ao criar config.js"
    exit 1
fi

# Iniciar nginx
exec "$@"
