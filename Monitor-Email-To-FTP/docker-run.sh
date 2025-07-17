#!/bin/bash

# Script para construir e executar o container Docker
# Uso: ./docker-run.sh [API_URL] [PORTA]

API_URL=${1:-"http://localhost:8080/api/v1/email-to-ftp"}
PORTA=${2:-"8081"}
CONTAINER_NAME="monitor-email-to-ftp"

echo "=== Construindo e executando Monitor Email to FTP ==="
echo "API URL: $API_URL"
echo "Porta: $PORTA"
echo "Container: $CONTAINER_NAME"
echo "======================================================"

# Parar e remover container existente se houver
if [ "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
    echo "Parando container existente..."
    docker stop $CONTAINER_NAME
fi

if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
    echo "Removendo container existente..."
    docker rm $CONTAINER_NAME
fi

# Construir imagem
echo "Construindo imagem Docker..."
docker build -t $CONTAINER_NAME .

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao construir a imagem Docker"
    exit 1
fi

# Executar container
echo "Iniciando container..."
docker run -d \
  --name $CONTAINER_NAME \
  -p $PORTA:80 \
  -e API_BASE_URL="$API_URL" \
  -e ENVIRONMENT="production" \
  -e REFRESH_INTERVAL=30000 \
  -e CONNECTION_TEST_INTERVAL=120000 \
  -e SCHEDULING_UPDATE_INTERVAL=10000 \
  -v /home/ricardo/microservicos/consultdg-microservices/Monitor-Email-To-FTP/logs:/var/log/nginx \
  $CONTAINER_NAME

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Container iniciado com sucesso!"
    echo "🌐 Acesse: http://localhost:$PORTA"
    echo "📊 Para ver logs: docker logs $CONTAINER_NAME"
    echo "🛑 Para parar: docker stop $CONTAINER_NAME"
    echo ""
    echo "=== Status do Container ==="
    docker ps | grep $CONTAINER_NAME
else
    echo "❌ ERRO: Falha ao iniciar o container"
    exit 1
fi
