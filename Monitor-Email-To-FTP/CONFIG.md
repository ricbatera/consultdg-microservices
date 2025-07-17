# Configuração de Variáveis de Ambiente

Este projeto foi configurado para suportar diferentes ambientes através de variáveis de configuração.

## Métodos de Configuração

### 1. Arquivo config.js (Recomendado)

O arquivo `config.js` contém as configurações da aplicação e é carregado antes do script principal.

#### Configuração Manual
Edite o arquivo `config.js` e modifique a propriedade `baseUrl`:

```javascript
window.API_CONFIG = {
    baseUrl: 'http://localhost:8080/api/v1/email-to-ftp', // Modifique aqui
    // ... outras configurações
};
```

#### Configuração por Ambiente
O sistema detecta automaticamente o ambiente baseado no hostname:
- **localhost/127.0.0.1**: ambiente de desenvolvimento
- **hostnames com 'staging' ou 'test'**: ambiente de staging  
- **outros hostnames**: ambiente de produção

### 2. Scripts de Deploy

Use os scripts de deploy para configurar automaticamente baseado no ambiente:

#### Windows (PowerShell/CMD)
```bash
deploy.bat development   # Para desenvolvimento
deploy.bat staging       # Para staging
deploy.bat production    # Para produção
```

#### Linux/macOS
```bash
chmod +x deploy.sh
./deploy.sh development   # Para desenvolvimento
./deploy.sh staging       # Para staging
./deploy.sh production    # Para produção
```

### 3. Arquivo .env (Referência)

Copie `.env.example` para `.env` e ajuste as configurações:

```bash
cp .env.example .env
```

Edite o arquivo `.env`:
```
API_BASE_URL=http://localhost:8080/api/v1/email-to-ftp
ENVIRONMENT=development
```

## Configurações Disponíveis

| Variável | Descrição | Padrão |
|----------|-----------|---------|
| `baseUrl` | URL base da API | `http://localhost:8080/api/v1/email-to-ftp` |
| `environment` | Ambiente atual | `development` |
| `refreshInterval` | Intervalo de atualização das stats (ms) | `30000` |
| `connectionTestInterval` | Intervalo de teste de conexões (ms) | `120000` |
| `schedulingUpdateInterval` | Intervalo de atualização do agendamento (ms) | `10000` |

## Exemplos de URLs por Ambiente

### Desenvolvimento
```
http://localhost:8080/api/v1/email-to-ftp
```

### Staging
```
https://staging-api.consultdg.com/api/v1/email-to-ftp
```

### Produção
```
https://api.consultdg.com/api/v1/email-to-ftp
```

## Como Funciona

1. O arquivo `config.js` é carregado primeiro no HTML
2. Define o objeto `window.API_CONFIG` com as configurações
3. O `script.js` usa essas configurações através de:
   ```javascript
   const API_BASE_URL = window.API_CONFIG?.baseUrl || 'fallback-url';
   ```

## Verificação

Para verificar se as configurações estão corretas, abra o Console do Navegador (F12) e observe as mensagens:
```
Ambiente detectado: development
URL da API: http://localhost:8080/api/v1/email-to-ftp
```

## Troubleshooting

### Problema: "API_CONFIG is not defined"
**Solução**: Certifique-se que o `config.js` está sendo carregado antes do `script.js` no HTML.

### Problema: Configurações não estão sendo aplicadas
**Solução**: Limpe o cache do navegador (Ctrl+F5) após modificar o `config.js`.

### Problema: Erro de CORS em produção
**Solução**: Verifique se a URL da API está correta e se o servidor permite requisições do domínio frontend.
