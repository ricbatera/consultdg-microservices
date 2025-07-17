# Exemplo de execução em produção com variáveis de ambiente

## 1. Configurar variáveis de ambiente no Linux:

```bash
# Configurar as variáveis
export GMAIL_USERNAME="seu_email@gmail.com"
export GMAIL_PASSWORD="sua_senha_app_16_caracteres"
export FTP_HOST="192.168.1.100"
export FTP_PORT="21"
export FTP_USERNAME="usuario_ftp"
export FTP_PASSWORD="senha_ftp"
export FTP_REMOTE_DIR="/uploads"
export EMAIL_PROCESSING_INTERVAL="300000"
export EMAIL_PROCESSING_MAX_MESSAGES="50"
export EMAIL_PROCESSING_MARK_AS_READ="true"

# Executar com profile de produção
java -jar -Dspring.profiles.active=prod email-to-ftp-service-0.0.1-SNAPSHOT.jar
```

## 2. Com systemd service:

```ini
# /etc/systemd/system/email-to-ftp.service
[Unit]
Description=Email to FTP Service
After=network.target

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/email-to-ftp
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod email-to-ftp-service.jar
Restart=always
RestartSec=10

# Variáveis de ambiente
Environment=GMAIL_USERNAME=seu_email@gmail.com
Environment=GMAIL_PASSWORD=sua_senha_app
Environment=FTP_HOST=192.168.1.100
Environment=FTP_PORT=21
Environment=FTP_USERNAME=usuario_ftp
Environment=FTP_PASSWORD=senha_ftp
Environment=FTP_REMOTE_DIR=/uploads
Environment=EMAIL_PROCESSING_INTERVAL=300000
Environment=EMAIL_PROCESSING_MAX_MESSAGES=50
Environment=EMAIL_PROCESSING_MARK_AS_READ=true

[Install]
WantedBy=multi-user.target
```

## 3. Com Docker:

```bash
# Build da imagem
docker build -t email-to-ftp:latest .

# Executar com variáveis de ambiente
docker run -d \
  --name email-to-ftp \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e GMAIL_USERNAME=seu_email@gmail.com \
  -e GMAIL_PASSWORD=sua_senha_app \
  -e FTP_HOST=192.168.1.100 \
  -e FTP_PORT=21 \
  -e FTP_USERNAME=usuario_ftp \
  -e FTP_PASSWORD=senha_ftp \
  -e FTP_REMOTE_DIR=/uploads \
  -e EMAIL_PROCESSING_INTERVAL=300000 \
  -e EMAIL_PROCESSING_MAX_MESSAGES=50 \
  -e EMAIL_PROCESSING_MARK_AS_READ=true \
  -p 8080:8080 \
  email-to-ftp:latest
```

## 4. Com Docker Compose:

```yaml
version: '3.8'
services:
  email-to-ftp:
    build: .
    container_name: email-to-ftp
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - GMAIL_USERNAME=seu_email@gmail.com
      - GMAIL_PASSWORD=sua_senha_app
      - FTP_HOST=192.168.1.100
      - FTP_PORT=21
      - FTP_USERNAME=usuario_ftp
      - FTP_PASSWORD=senha_ftp
      - FTP_REMOTE_DIR=/uploads
      - EMAIL_PROCESSING_INTERVAL=300000
      - EMAIL_PROCESSING_MAX_MESSAGES=50
      - EMAIL_PROCESSING_MARK_AS_READ=true
    restart: unless-stopped
    
    # Alternativa: usar arquivo de variáveis
    # env_file:
    #   - .env.production
```

## 5. Arquivo .env.production (para Docker):

```bash
# .env.production
SPRING_PROFILES_ACTIVE=prod
GMAIL_USERNAME=seu_email@gmail.com
GMAIL_PASSWORD=sua_senha_app
FTP_HOST=192.168.1.100
FTP_PORT=21
FTP_USERNAME=usuario_ftp
FTP_PASSWORD=senha_ftp
FTP_REMOTE_DIR=/uploads
EMAIL_PROCESSING_INTERVAL=300000
EMAIL_PROCESSING_MAX_MESSAGES=50
EMAIL_PROCESSING_MARK_AS_READ=true
```

## Vantagens desta abordagem:

✅ **Segurança**: Não há arquivos .env com credenciais nos containers/servidores
✅ **Flexibilidade**: Fácil mudança de configuração sem rebuild
✅ **Isolamento**: Cada ambiente (dev/test/prod) tem suas próprias configurações
✅ **Integração**: Funciona nativamente com Docker, Kubernetes, etc.
✅ **Debugging**: Logs mostram claramente se está usando .env (dev) ou variáveis de ambiente (prod)

## Detecção automática:

O código agora detecta automaticamente:
- **Desenvolvimento**: Usa arquivo .env
- **Produção** (profile prod/production): Usa só variáveis de ambiente do sistema
