# Monitor Email to FTP Service

Interface web moderna para monitoramento do serviço Email to FTP da ConsultDG.

## Características

- **Design responsivo** seguindo o guia de marca da ConsultDG
- **Monitoramento em tempo real** do status do serviço
- **Estatísticas detalhadas** de processamento de emails e anexos
- **Gráficos interativos** de atividade nas últimas 24 horas
- **Controles manuais** para processamento e testes
- **Log de atividades** em tempo real
- **Auto-refresh** automático dos dados

## Funcionalidades

### 📊 Dashboard Principal
- Status em tempo real do serviço (Online/Offline)
- Indicadores de conectividade Gmail e FTP
- Estatísticas de emails processados e anexos enviados
- Gráfico de atividade das últimas 24 horas

### 🔧 Controles Manuais
- **Processar Emails**: Executa processamento manual de emails
- **Testar Conexões**: Verifica conectividade com Gmail e FTP
- **Resetar Estatísticas**: Zera os contadores de estatísticas

### 📋 Monitoramento
- Log de atividades em tempo real
- Atualização automática a cada 30 segundos
- Notificações visuais para ações do usuário
- Indicadores de tendência nas estatísticas

## Como Usar

### 1. Pré-requisitos
- Serviço Email to FTP rodando em `localhost:8080`
- Navegador web moderno com suporte a ES6+

### 2. Executando
1. Certifique-se que o serviço backend está rodando
2. Abra o arquivo `index.html` em um navegador
3. A página irá automaticamente conectar ao serviço e exibir os dados

### 3. Funcionalidades da Interface

#### Status Cards
- **Gmail**: Mostra o status da conexão IMAP com o Gmail
- **FTP**: Exibe o status da conexão com o servidor FTP  
- **Processamento**: Indica se o processamento automático está ativo

#### Estatísticas
- **Emails Processados**: Total de emails processados desde o último reset
- **Anexos Enviados**: Número de anexos enviados via FTP
- **Falhas de Upload**: Quantidade de falhas de upload
- **Última Execução**: Horário da última execução do processamento

#### Controles
- **Processar Emails**: Força o processamento manual de emails
- **Testar Conexões**: Executa teste de conectividade
- **Atualizar**: Atualiza manualmente as estatísticas
- **Resetar**: Zera as estatísticas (requer confirmação)

## Estrutura dos Arquivos

```
Monitor-Email-To-FTP/
├── index.html          # Página principal
├── styles.css          # Estilos CSS seguindo guia de marca
├── script.js           # Lógica JavaScript e integração com API
├── README.md           # Este arquivo
└── assets/             # Recursos
    └── img/            # Imagens da marca ConsultDG
        ├── DG_LogotipoCompleto_RGB.png
        ├── DG_Simbolo_RGB.png
        └── ...
```

## API Endpoints Utilizados

A interface consome os seguintes endpoints do serviço:

- `GET /api/v1/email-to-ftp/health` - Status do serviço
- `GET /api/v1/email-to-ftp/stats` - Estatísticas de processamento
- `POST /api/v1/email-to-ftp/process` - Processamento manual
- `POST /api/v1/email-to-ftp/stats/reset` - Reset das estatísticas
- `GET /api/v1/email-to-ftp/test-connections` - Teste de conexões

## Personalização

### Cores e Fontes
O design segue o guia de marca da ConsultDG:
- **Cores primárias**: #0066CC, #004899
- **Cor de destaque**: #33B5E5
- **Fonte**: DM Sans (com fallback para system fonts)

### Configuração da API
Para alterar a URL base da API, edite a variável `API_BASE_URL` no arquivo `script.js`:

```javascript
const API_BASE_URL = 'http://localhost:8080/api/v1/email-to-ftp';
```

### Auto-refresh
Os intervalos de atualização podem ser ajustados no arquivo `script.js`:

```javascript
// Atualizar stats a cada 30 segundos
setInterval(loadStats, 30000);

// Verificar saúde do serviço a cada 60 segundos  
setInterval(checkServiceHealth, 60000);
```

## Compatibilidade

- **Navegadores**: Chrome 60+, Firefox 55+, Safari 12+, Edge 79+
- **Resolução**: Responsivo para desktop, tablet e mobile
- **JavaScript**: ES6+ (async/await, fetch API, etc.)

## Troubleshooting

### Serviço não conecta
1. Verifique se o serviço está rodando em `localhost:8080`
2. Confirme que não há bloqueio de CORS
3. Verifique o console do navegador para erros

### Dados não atualizam
1. Verifique a conectividade de rede
2. Confirme que os endpoints da API estão respondendo
3. Verifique se há erros no console do navegador

### Interface não carrega corretamente
1. Verifique se todos os arquivos estão no mesmo diretório
2. Confirme que as imagens estão no caminho correto
3. Teste em um navegador diferente

## Desenvolvimento

Para modificar ou estender a interface:

1. **HTML** (`index.html`): Estrutura da página
2. **CSS** (`styles.css`): Estilos e layout responsivo  
3. **JavaScript** (`script.js`): Lógica de negócio e integração com API

### Adicionando novas funcionalidades
1. Adicione o HTML necessário na seção apropriada
2. Implemente os estilos CSS seguindo o padrão existente
3. Adicione a lógica JavaScript para integração com a API
4. Teste em diferentes resoluções e navegadores

## Licença

© 2025 ConsultDG - Todos os direitos reservados.
