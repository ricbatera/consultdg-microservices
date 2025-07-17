#!/bin/bash

# Script para configurar e fazer deploy da aplicação
# Use: ./deploy.sh [development|staging|production]

ENVIRONMENT=${1:-development}

echo "Configurando ambiente: $ENVIRONMENT"

# Definir URLs baseadas no ambiente
case $ENVIRONMENT in
    "development")
        API_URL="http://localhost:8080/api/v1/email-to-ftp"
        ;;
    "staging")
        API_URL="https://staging-api.exemplo.com/api/v1/email-to-ftp"
        ;;
    "production")
        API_URL="https://api.exemplo.com/api/v1/email-to-ftp"
        ;;
    *)
        echo "Ambiente inválido. Use: development, staging ou production"
        exit 1
        ;;
esac

# Criar arquivo de configuração dinâmico
cat > config.js << EOF
// Configurações da aplicação - Gerado automaticamente
window.API_CONFIG = {
    baseUrl: '$API_URL',
    environment: '$ENVIRONMENT',
    refreshInterval: 30000,
    connectionTestInterval: 120000,
    schedulingUpdateInterval: 10000
};

console.log('Configuração carregada para ambiente:', window.API_CONFIG.environment);
console.log('URL da API:', window.API_CONFIG.baseUrl);
EOF

echo "Configuração criada com sucesso!"
echo "Ambiente: $ENVIRONMENT"
echo "URL da API: $API_URL"
echo ""
echo "Para usar em outros ambientes:"
echo "  ./deploy.sh development"
echo "  ./deploy.sh staging" 
echo "  ./deploy.sh production"
