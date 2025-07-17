# Docker Deploy - Monitor Email to FTP

Este documento descreve como fazer deploy da aplicação usando Docker com Nginx.

## 🚀 Comandos Rápidos

### Comando Docker Direto
```bash
# Construir a imagem
docker build -t monitor-email-to-ftp .

# Executar o container
docker run -d \
  --name monitor-email-to-ftp \
  -p 8081:80 \
  -e API_BASE_URL="http://sua-api.com/api/v1/email-to-ftp" \
  -v /home/ricardo/microservicos/consultdg-microservices/Monitor-Email-To-FTP:/usr/share/nginx/html \
  monitor-email-to-ftp
```

### Usando Docker Compose
```bash
# Editar docker-compose.yml com suas configurações
# Depois executar:
docker-compose up -d
```

### Usando Scripts Automatizados
```bash
# Linux/macOS
chmod +x docker-run.sh
./docker-run.sh "http://sua-api.com/api/v1/email-to-ftp" 8081

# Windows
docker-run.bat "http://sua-api.com/api/v1/email-to-ftp" 8081
```

## 📁 Mapeamento de Volumes

### Volume para arquivos do projeto:
```
-v /home/ricardo/microservicos/consultdg-microservices/Monitor-Email-To-FTP:/usr/share/nginx/html
```

### Volume para logs (opcional):
```
-v /home/ricardo/microservicos/consultdg-microservices/Monitor-Email-To-FTP/logs:/var/log/nginx
```

## 🔧 Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|---------|
| `API_BASE_URL` | URL da API backend | `http://localhost:8080/api/v1/email-to-ftp` |
| `ENVIRONMENT` | Ambiente (development/staging/production) | `production` |
| `REFRESH_INTERVAL` | Intervalo de atualização (ms) | `30000` |
| `CONNECTION_TEST_INTERVAL` | Intervalo teste conexões (ms) | `120000` |
| `SCHEDULING_UPDATE_INTERVAL` | Intervalo agendamento (ms) | `10000` |

## 🌐 Exemplos de Deploy

### Ambiente de Desenvolvimento
```bash
docker run -d \
  --name monitor-email-to-ftp-dev \
  -p 8081:80 \
  -e API_BASE_URL="http://localhost:8080/api/v1/email-to-ftp" \
  -e ENVIRONMENT="development" \
  -v /home/ricardo/microservicos/consultdg-microservices/Monitor-Email-To-FTP:/usr/share/nginx/html \
  monitor-email-to-ftp
```

### Ambiente de Produção
```bash
docker run -d \
  --name monitor-email-to-ftp-prod \
  -p 80:80 \
  -e API_BASE_URL="https://api.consultdg.com/api/v1/email-to-ftp" \
  -e ENVIRONMENT="production" \
  -v /home/ricardo/microservicos/consultdg-microservices/Monitor-Email-To-FTP:/usr/share/nginx/html \
  -v /home/ricardo/microservicos/consultdg-microservices/Monitor-Email-To-FTP/logs:/var/log/nginx \
  --restart unless-stopped \
  monitor-email-to-ftp
```

### Com SSL/HTTPS (usando proxy reverso)
```bash
# Usar com Traefik ou nginx-proxy
docker run -d \
  --name monitor-email-to-ftp \
  -e API_BASE_URL="https://api.consultdg.com/api/v1/email-to-ftp" \
  -e ENVIRONMENT="production" \
  -v /home/ricardo/microservicos/consultdg-microservices/Monitor-Email-To-FTP:/usr/share/nginx/html \
  --label "traefik.enable=true" \
  --label "traefik.http.routers.monitor.rule=Host(\`monitor.consultdg.com\`)" \
  --label "traefik.http.routers.monitor.tls=true" \
  --network traefik \
  monitor-email-to-ftp
```

## 🔍 Comandos de Monitoramento

### Ver logs do container
```bash
docker logs -f monitor-email-to-ftp
```

### Entrar no container
```bash
docker exec -it monitor-email-to-ftp sh
```

### Ver status
```bash
docker ps | grep monitor-email-to-ftp
```

### Ver configuração gerada
```bash
docker exec monitor-email-to-ftp cat /usr/share/nginx/html/config.js
```

## 🛠️ Troubleshooting

### Problema: Container não inicia
```bash
# Verificar logs
docker logs monitor-email-to-ftp

# Verificar se a porta está em uso
netstat -tlnp | grep :8081
```

### Problema: Configuração não está sendo aplicada
```bash
# Verificar variáveis de ambiente
docker exec monitor-email-to-ftp env | grep API

# Verificar arquivo de configuração
docker exec monitor-email-to-ftp cat /usr/share/nginx/html/config.js
```

### Problema: Erro de CORS
- Verifique se a `API_BASE_URL` está correta
- Certifique-se que o backend permite requisições do frontend

### Problema: Volume não está funcionando
```bash
# Verificar se o caminho existe
ls -la /home/ricardo/microservicos/consultdg-microservices/Monitor-Email-To-FTP

# Verificar permissões
sudo chown -R 1000:1000 /home/ricardo/microservicos/consultdg-microservices/Monitor-Email-To-FTP
```

## 🔄 Atualização da Aplicação

Para atualizar a aplicação:

```bash
# Parar container
docker stop monitor-email-to-ftp

# Remover container
docker rm monitor-email-to-ftp

# Rebuild imagem
docker build -t monitor-email-to-ftp .

# Executar novamente
./docker-run.sh "http://sua-api.com/api/v1/email-to-ftp" 8081
```

## 📦 Estrutura do Container

```
/usr/share/nginx/html/          # Arquivos da aplicação
├── index.html
├── script.js
├── styles.css
├── config.js                   # Gerado automaticamente
├── config.template.js          # Template
└── assets/

/etc/nginx/conf.d/              # Configuração Nginx
└── nginx.conf

/var/log/nginx/                 # Logs
├── access.log
└── error.log
```
