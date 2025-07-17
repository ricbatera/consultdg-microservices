# Email to FTP Service

Serviço Spring Boot que conecta ao Gmail via IMAP, busca emails com anexos e os envia via FTP.

## Funcionalidades

- Conecta ao Gmail via IMAP usando autenticação por senha de aplicativo
- Busca emails não lidos com anexos
- Faz download dos anexos
- Envia anexos via FTP organizados por data
- Processamento automático agendado
- API REST para monitoramento e controle manual
- Marca emails como lidos após processamento (configurável)

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
# Intervalo de processamento (em milissegundos)
email.processing.interval=300000

# Máximo de emails processados por vez
email.processing.max-messages=50

# Marcar emails como lidos após processamento
email.processing.mark-as-read=true
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

## API REST

O serviço expõe as seguintes endpoints:

### GET /api/v1/email-to-ftp/health
Verifica o status do serviço

### POST /api/v1/email-to-ftp/process
Inicia o processamento manual de emails

### GET /api/v1/email-to-ftp/stats
Retorna estatísticas de processamento

### POST /api/v1/email-to-ftp/stats/reset
Zera as estatísticas de processamento

### GET /api/v1/email-to-ftp/test-connections
Testa as conexões com Gmail e FTP

## Estrutura dos Arquivos no FTP

Os arquivos são organizados da seguinte forma:

```
/uploads/
├── 2024-01-15/
│   ├── 143022_Relatorio_Vendas_documento.pdf
│   └── 143055_Planilha_Custos_dados.xlsx
├── 2024-01-16/
│   └── 090130_Backup_Sistema_arquivo.zip
```

Formato do nome: `HHMMSS_AssuntoEmail_NomeArquivo`

## Logs

O serviço gera logs detalhados sobre:
- Conexões com Gmail e FTP
- Emails processados
- Anexos enviados
- Erros e falhas

## Segurança

- Use sempre senhas de aplicativo do Gmail (nunca a senha principal)
- Configure adequadamente as credenciais FTP
- Mantenha as variáveis de ambiente seguras
- Monitore os logs para detectar problemas de acesso

## Troubleshooting

### Erro de autenticação Gmail
- Verifique se a autenticação de 2 fatores está habilitada
- Confirme se está usando a senha de aplicativo (16 caracteres)
- Teste com um cliente de email como Thunderbird

### Erro de conexão FTP
- Verifique as credenciais FTP
- Teste a conectividade de rede
- Confirme se o diretório remoto existe e tem permissões

### Emails não são processados
- Verifique se há emails não lidos com anexos
- Confirme as configurações de intervalo de processamento
- Analise os logs para identificar erros

## Monitoramento

Use o endpoint `/api/v1/email-to-ftp/stats` para monitorar:
- Número de emails processados
- Número de anexos enviados
- Número de falhas de upload

## Desenvolvimento

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

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Faça push para a branch
5. Abra um Pull Request
