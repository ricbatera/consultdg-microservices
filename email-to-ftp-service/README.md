# Email to FTP Service

Serviço Spring Boot que conecta ao Gmail via IMAP, busca emails com anexos e os envia via FTP de forma automática e robusta.

## 🚀 Funcionalidades

- ✅ **Conecta ao Gmail via IMAP** usando autenticação por senha de aplicativo
- ✅ **Busca emails não lidos** com anexos automaticamente
- ✅ **Download inteligente de anexos** com logs detalhados
- ✅ **Upload via FTP** organizados por data com nomes únicos
- ✅ **Processamento automático agendado** configurável
- ✅ **API REST completa** para monitoramento e controle
- ✅ **Proteção contra travamentos** com timeouts e recovery
- ✅ **Logs detalhados** para diagnóstico e troubleshooting
- ✅ **Processamento de múltiplos anexos** por email
- ✅ **Controle de concorrência** para evitar conflitos
- ✅ **Marca emails como lidos** após processamento (configurável)
- 🆕 **Filtro de tipos de arquivo** - aceita apenas PDF e XML
- 🆕 **Validação de nomenclatura PDF** - arquivos devem começar com P_ ou X_
- 🆕 **Resposta automática** - solicita correção de nomes inválidos

## Configuração

### 1. Configuração do Gmail

Para usar este serviço com Gmail, você precisa:

1. Habilitar a autenticação de 2 fatores na sua conta Google
2. Gerar uma senha de aplicativo:
   - Acesse https://myaccount.google.com/security
   - Clique em "Senhas de aplicativo"
   - Selecione "Aplicativo personalizado" e digite um nome
   - Use a senha gerada (16 caracteres) na configuração

### 2. Configuração do Aplicativo

Configure as seguintes variáveis de ambiente ou edite o `application.properties`:

```properties
# Gmail
GMAIL_USERNAME=seu-email@gmail.com
GMAIL_PASSWORD=sua-senha-de-aplicativo-16-caracteres

# FTP
FTP_HOST=seu-servidor-ftp.com
FTP_PORT=21
FTP_USERNAME=seu-usuario-ftp
FTP_PASSWORD=sua-senha-ftp
FTP_REMOTE_DIR=/uploads
```

### 3. Configurações Avançadas

Você pode ajustar as seguintes configurações no `application.properties`:

```properties
# Intervalo de processamento (em milissegundos) - 5 minutos
email.processing.interval=300000

# Máximo de emails processados por vez
email.processing.max-messages=50

# Marcar emails como lidos após processamento
email.processing.mark-as-read=true

# Timeout para processamento (em milissegundos) - 5 minutos
email.processing.timeout=300000

# 🆕 Configurações de validação de anexos
validation.enabled=true
validation.allowed-extensions=pdf,xml
validation.pdf.required-prefixes=P_,X_
validation.send-correction-emails=true

# 🆕 Configurações de resposta automática por email
email.response.enabled=true
email.response.from-name=Sistema Processamento Automático ConsultDG

# Configurações de log para diagnóstico
logging.level.br.com.consultdg=DEBUG
```

## Execução

### Desenvolvimento

```bash
./mvnw spring-boot:run
```

### Produção

```bash
./mvnw clean package
java -jar target/email-to-ftp-service-0.0.1-SNAPSHOT.jar
```

## 📡 API REST

O serviço expõe as seguintes endpoints:

### Processamento
- **POST** `/api/v1/email-to-ftp/process` - Inicia o processamento manual de emails

### Monitoramento
- **GET** `/api/v1/email-to-ftp/health` - Verifica o status do serviço
- **GET** `/api/v1/email-to-ftp/stats` - Retorna estatísticas de processamento
- **GET** `/api/v1/email-to-ftp/status` - 🆕 Status detalhado do processamento atual
- **GET** `/api/v1/email-to-ftp/test-connections` - Testa conexões (Gmail e FTP)

### Controle
- **POST** `/api/v1/email-to-ftp/stats/reset` - Zera as estatísticas
- **POST** `/api/v1/email-to-ftp/force-reset` - 🆕 Força reset em caso de travamento

### 🆕 Validação e Teste
- **POST** `/api/v1/email-to-ftp/test-validation?fileName=arquivo.pdf` - Testa validação de arquivo
- **POST** `/api/v1/email-to-ftp/test-email-response` - Testa configuração de envio de email

### 🆕 Exemplo de resposta do `/status`:
```json
{
  "isProcessing": true,
  "processingTimeMs": 45000,
  "processingTimeSeconds": 45,
  "stats": {
    "processedEmails": 3,
    "processedAttachments": 8,
    "failedUploads": 0,
    "rejectedAttachments": 2,
    "correctionEmailsSent": 1
  },
  "timestamp": 1642194456789
}
```

### 🆕 Exemplo de resposta do `/test-validation`:
```json
{
  "fileName": "documento.pdf",
  "valid": false,
  "reason": "Nome do arquivo PDF deve começar com 'P_' ou 'X_'.",
  "timestamp": 1642194456789
}
```

## 📁 Estrutura dos Arquivos no FTP

Os arquivos são organizados da seguinte forma:

```
/uploads/
├── 2025-07-17/
│   ├── 143022_ENC__Formulario_RETIRO_UMADEB_2025_Formulário RETIRO UMADEB 2025.pdf
│   ├── 143022_ENC__Formulario_RETIRO_UMADEB_2025_image001.png
│   └── 143055_Relatorio_Vendas_documento.xlsx
├── 2025-07-18/
│   └── 090130_Backup_Sistema_arquivo.zip
```

**Formato do nome:** `HHMMSS_AssuntoLimpo_NomeArquivoOriginal`
- `HHMMSS`: Horário de recebimento do email
- `AssuntoLimpo`: Assunto do email (caracteres especiais removidos)
- `NomeArquivoOriginal`: Nome original do anexo

## 📋 Logs

O serviço gera logs **muito detalhados** para facilitar o diagnóstico:

### 🔍 Logs de Processamento
```
=== INICIANDO PROCESSAMENTO DO EMAIL ===
Assunto: 'ENC: Formulário Retiro UMADEB 2025'
De: sender@example.com
Total de anexos: 3
>>> Processando anexo 1/3: 'Formulário RETIRO UMADEB 2025.pdf'
    Tamanho: 2048576 bytes
✓ Anexo 'Formulário RETIRO UMADEB 2025.pdf' enviado com SUCESSO (1/3)
=== FIM DO PROCESSAMENTO DO EMAIL ===
```

### 🚨 Logs de Erro Melhorados
```
ERROR - Falha ao enviar anexo 'documento.pdf' do email 'Relatório Mensal' para FTP. 
        Motivo: Erro na transferência do arquivo ou espaço insuficiente no servidor
ERROR - Erro de IO durante upload FTP do anexo 'planilha.xlsx' do email 'Dados Vendas'. 
        Motivo: SocketTimeoutException - Detalhes: Read timed out
```

### 📊 Logs de Timing
```
INFO - Busca de emails concluída em 1250ms
INFO - Email 'Relatório' processado em 3420ms
INFO - Tempo total de processamento: 5680ms
```

## Segurança

- Use sempre senhas de aplicativo do Gmail (nunca a senha principal)
- Configure adequadamente as credenciais FTP
- Mantenha as variáveis de ambiente seguras
- Monitore os logs para detectar problemas de acesso

## 🛠️ Troubleshooting

### ❌ Erro de autenticação Gmail
- Verifique se a autenticação de 2 fatores está habilitada
- Confirme se está usando a senha de aplicativo (16 caracteres)
- Teste com um cliente de email como Thunderbird

### ❌ Erro de conexão FTP
- Verifique as credenciais FTP
- Teste a conectividade de rede
- Confirme se o diretório remoto existe e tem permissões

### ❌ Emails não são processados
- Use `/api/v1/email-to-ftp/test-connections` para verificar conectividade
- Verifique se há emails não lidos com anexos
- Confirme as configurações de intervalo de processamento
- Analise os logs DEBUG para identificar erros

### 🚨 **NOVO**: Sistema Travado?
Se o processamento parecer travado:

1. **Verificar status:**
   ```bash
   curl http://localhost:8080/api/v1/email-to-ftp/status
   ```

2. **Se `isProcessing=true` há muito tempo:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/email-to-ftp/force-reset
   ```

3. **Processar novamente:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/email-to-ftp/process
   ```

### 🔍 Logs de Diagnóstico
Para análise detalhada, ative logs DEBUG:
```properties
logging.level.br.com.consultdg=DEBUG
```

Isso mostrará:
- Cada etapa do processamento de emails
- Extração detalhada de anexos
- Timing de cada operação
- Problemas de conectividade

## 📊 Monitoramento

### Estatísticas Básicas
Use `/api/v1/email-to-ftp/stats` para monitorar:
- Número de emails processados
- Número de anexos enviados  
- Número de falhas de upload

### 🆕 Monitoramento Avançado
Use `/api/v1/email-to-ftp/status` para:
- Ver se está processando atualmente
- Tempo de processamento em andamento
- Detectar possíveis travamentos

### 🔄 Processamento Automático
- **Intervalo padrão:** 5 minutos
- **Proteção contra travamento:** Timeout de 5 minutos
- **Controle de concorrência:** Evita processamento simultâneo
- **Recovery automático:** Reset de flags em caso de erro

## 🚀 Melhorias Recentes

### ✅ v2.0 - Sistema Robusto e Monitorável

#### 🔧 **Correções de Bugs Críticos:**
- **Problema de emails múltiplos anexos não processados** - RESOLVIDO
- **Travamentos durante processamento** - RESOLVIDO com timeouts
- **Conflito entre teste de conexão e processamento** - RESOLVIDO

#### 🆕 **Novas Funcionalidades:**
- **Logs super detalhados** com timing e diagnóstico
- **Sistema de proteção contra travamentos**
- **Endpoints de monitoramento avançado** (`/status`, `/force-reset`)
- **Controle de concorrência** para evitar processamento simultâneo
- **Recovery automático** com timeouts configuráveis

#### 📈 **Melhorias de Performance:**
- **Processamento otimizado** de emails com múltiplos anexos
- **Logs estruturados** para facilitar debugging
- **Tratamento robusto de erros** com mensagens específicas
- **Timeout configurável** para evitar travamentos indefinidos

## 🎯 Desenvolvimento

### Estrutura do Projeto

```
src/main/java/br/com/consultdg/email_to_ftp_service/
├── config/           # Configurações
├── controller/       # Controllers REST
├── model/           # Modelos de dados
├── service/         # Lógica de negócio
└── EmailToFtpServiceApplication.java
```

### Executar Testes

```bash
./mvnw test
```

## 📋 Exemplos de Uso

### 1. Monitoramento Básico
```bash
# Verificar se o serviço está rodando
curl http://localhost:8080/api/v1/email-to-ftp/health

# Ver estatísticas
curl http://localhost:8080/api/v1/email-to-ftp/stats
```

### 2. Processamento Manual
```bash
# Processar emails agora
curl -X POST http://localhost:8080/api/v1/email-to-ftp/process

# Verificar se está processando
curl http://localhost:8080/api/v1/email-to-ftp/status
```

### 3. Diagnóstico de Problemas
```bash
# Testar conexões
curl http://localhost:8080/api/v1/email-to-ftp/test-connections

# Testar validação de arquivo
curl -X POST "http://localhost:8080/api/v1/email-to-ftp/test-validation?fileName=P_123456_documento.pdf"
curl -X POST "http://localhost:8080/api/v1/email-to-ftp/test-validation?fileName=documento.pdf"

# Testar configuração de email de resposta
curl -X POST http://localhost:8080/api/v1/email-to-ftp/test-email-response

# Se travado, forçar reset
curl -X POST http://localhost:8080/api/v1/email-to-ftp/force-reset

# Zerar estatísticas
curl -X POST http://localhost:8080/api/v1/email-to-ftp/stats/reset
```

### 🆕 4. Testando Validação de Anexos
```bash
# Exemplos de validação de arquivos
curl -X POST "http://localhost:8080/api/v1/email-to-ftp/test-validation?fileName=P_123456_relatorio.pdf"
# Resposta: {"fileName": "P_123456_relatorio.pdf", "valid": true, "reason": "Arquivo válido"}

curl -X POST "http://localhost:8080/api/v1/email-to-ftp/test-validation?fileName=X_789012_documento.pdf"  
# Resposta: {"fileName": "X_789012_documento.pdf", "valid": true, "reason": "Arquivo válido"}

curl -X POST "http://localhost:8080/api/v1/email-to-ftp/test-validation?fileName=relatorio.pdf"
# Resposta: {"fileName": "relatorio.pdf", "valid": false, "reason": "Nome do arquivo PDF deve começar com 'P_' ou 'X_'."}

curl -X POST "http://localhost:8080/api/v1/email-to-ftp/test-validation?fileName=dados.xml"
# Resposta: {"fileName": "dados.xml", "valid": true, "reason": "Arquivo válido"}

curl -X POST "http://localhost:8080/api/v1/email-to-ftp/test-validation?fileName=planilha.xlsx"
# Resposta: {"fileName": "planilha.xlsx", "valid": false, "reason": "Tipo de arquivo não permitido. Apenas arquivos PDF e XML são aceitos."}
```

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Faça push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 📞 Suporte

- **Issues:** Use o GitHub Issues para reportar bugs ou solicitar features
- **Logs:** Sempre ative `logging.level.br.com.consultdg=DEBUG` para diagnóstico
- **Monitoramento:** Use os endpoints `/status` e `/stats` para acompanhar o serviço

---

**Versão:** 2.0  
**Última atualização:** Julho 2025  
**Status:** ✅ Estável e em produção
