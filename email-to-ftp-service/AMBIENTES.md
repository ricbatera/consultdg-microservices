# Configuração de Ambientes - Email to FTP Service

Este projeto está configurado com **profiles específicos** para cada ambiente, seguindo as melhores práticas do Spring Boot.

## 📁 Arquivos de Configuração

| Arquivo | Ambiente | Descrição |
|---------|----------|-----------|
| `application.properties` | **Base** | Configurações básicas compartilhadas |
| `application-dev.properties` | **Desenvolvimento** | Configurações para desenvolvimento local |
| `application-hom.properties` | **Homologação** | Configurações para ambiente de testes |
| `application-prod.properties` | **Produção** | Configurações otimizadas para produção |

## 🚀 Como Executar em Cada Ambiente

### 💻 **Desenvolvimento (padrão)**
```bash
# Com arquivo .env (recomendado para dev)
mvn spring-boot:run

# Ou explicitamente
mvn spring-boot:run -Dspring.profiles.active=dev
```

### 🧪 **Homologação**
```bash
# Com variáveis de ambiente
export SPRING_PROFILES_ACTIVE=hom
export GMAIL_USERNAME=teste@empresa.com
export GMAIL_PASSWORD=senha_teste
export FTP_HOST=192.168.1.100
# ... outras variáveis

mvn spring-boot:run
```

### 🏭 **Produção**
```bash
# Com variáveis de ambiente (recomendado)
export SPRING_PROFILES_ACTIVE=prod
export GMAIL_USERNAME=producao@empresa.com
export GMAIL_PASSWORD=senha_producao
export FTP_HOST=ftp.empresa.com
# ... outras variáveis

java -jar target/email-to-ftp-service-0.0.1-SNAPSHOT.jar
```

## 🐳 Docker

### Desenvolvimento
```bash
docker run -e SPRING_PROFILES_ACTIVE=dev -v $(pwd)/.env:/app/.env email-to-ftp:latest
```

### Homologação
```bash
docker run -e SPRING_PROFILES_ACTIVE=hom \
  -e GMAIL_USERNAME=teste@empresa.com \
  -e GMAIL_PASSWORD=senha_teste \
  email-to-ftp:latest
```

### Produção
```bash
docker run -e SPRING_PROFILES_ACTIVE=prod \
  -e GMAIL_USERNAME=producao@empresa.com \
  -e GMAIL_PASSWORD=senha_producao \
  -e FTP_HOST=ftp.empresa.com \
  email-to-ftp:latest
```

## ⚙️ Variáveis de Ambiente

### 📧 **Email (Gmail)**
- `GMAIL_USERNAME` - Email da conta Gmail
- `GMAIL_PASSWORD` - Senha de aplicativo (16 caracteres)

### 📁 **FTP**
- `FTP_HOST` - Servidor FTP
- `FTP_PORT` - Porta do FTP (padrão: 21)
- `FTP_USERNAME` - Usuário FTP
- `FTP_PASSWORD` - Senha FTP
- `FTP_REMOTE_DIR` - Diretório remoto (padrão: /)

### ⚡ **Processamento**
- `EMAIL_PROCESSING_INTERVAL` - Intervalo entre processamentos (ms)
- `EMAIL_PROCESSING_MAX_MESSAGES` - Máximo de emails por processamento
- `EMAIL_PROCESSING_MARK_AS_READ` - Marcar emails como lidos (true/false)
- `EMAIL_PROCESSING_TIMEOUT` - Timeout do processamento (ms)

### ✅ **Validação**
- `VALIDATION_ENABLED` - Habilitar validação (true/false)
- `VALIDATION_ALLOWED_EXTENSIONS` - Extensões permitidas (pdf,xml)
- `VALIDATION_PDF_PREFIXES` - Prefixos obrigatórios para PDF (P_,X_)
- `VALIDATION_SEND_CORRECTION_EMAILS` - Enviar emails de correção (true/false)

### 📬 **Resposta Automática**
- `EMAIL_RESPONSE_ENABLED` - Habilitar resposta automática (true/false)
- `EMAIL_RESPONSE_FROM_NAME` - Nome do remetente das respostas

### 🔧 **Sistema**
- `SERVER_PORT` - Porta da aplicação (padrão: 8080)
- `MANAGEMENT_PORT` - Porta do management (padrão: 8081)

## 🎯 Características por Ambiente

### 💻 **Desenvolvimento**
- ✅ Carrega arquivo `.env` automaticamente
- ✅ Swagger UI habilitado
- ✅ DevTools habilitado
- ✅ Logs detalhados (DEBUG)
- ✅ Live reload
- ✅ Todos os endpoints do Actuator expostos

### 🧪 **Homologação**
- ✅ Swagger UI habilitado para testes
- ⚡ Logs intermediários (INFO/WARN)
- 🔒 Endpoints limitados do Actuator
- 🔒 Management em porta separada

### 🏭 **Produção**
- ❌ Swagger UI **desabilitado**
- ❌ Arquivo `.env` **ignorado** (usa apenas variáveis de ambiente)
- ⚡ Logs mínimos (WARN/ERROR)
- 🔒 Apenas endpoints essenciais do Actuator
- 🔒 Configurações de performance otimizadas
- 🔒 Graceful shutdown habilitado

## 🔍 Verificação de Configuração

Para verificar qual profile está ativo e as configurações carregadas:

```bash
# Health check
curl http://localhost:8080/actuator/health

# Informações da aplicação
curl http://localhost:8080/actuator/info

# Configurações (só em dev/hom)
curl http://localhost:8080/actuator/configprops
```

## 🚨 Observações Importantes

1. **Produção**: Nunca use arquivo `.env` em produção. Use apenas variáveis de ambiente do sistema.

2. **Segurança**: Senhas e credenciais devem sempre vir de variáveis de ambiente, não hardcoded.

3. **Logs**: Em produção, os logs são mínimos para performance. Para debug, use temporariamente o profile de homologação.

4. **Swagger**: Está desabilitado em produção por questões de segurança.

5. **Variáveis Obrigatórias**: `GMAIL_USERNAME`, `GMAIL_PASSWORD`, `FTP_HOST`, `FTP_USERNAME`, `FTP_PASSWORD` são obrigatórias em todos os ambientes.
