# Email to FTP Service - Swagger Documentation

## Sobre o Swagger

O Swagger UI foi implementado para fornecer uma interface interativa e documentação completa das APIs do serviço Email to FTP.

## Como Acessar

### Desenvolvimento Local

Após iniciar a aplicação, você pode acessar:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8080/api-docs
- **API Docs (YAML)**: http://localhost:8080/api-docs.yaml

### Produção

- **Swagger UI**: https://api.consultdg.com.br/swagger-ui.html
- **API Docs (JSON)**: https://api.consultdg.com.br/api-docs

## Funcionalidades Disponíveis

### Endpoints Documentados

1. **GET /api/v1/email-to-ftp/health**
   - Verificar saúde do serviço
   - Retorna status do serviço

2. **POST /api/v1/email-to-ftp/process**
   - Iniciar processamento manual de emails
   - Força o processamento imediato

3. **GET /api/v1/email-to-ftp/stats**
   - Obter estatísticas de processamento
   - Retorna métricas detalhadas

4. **POST /api/v1/email-to-ftp/stats/reset**
   - Zerar estatísticas
   - Reseta contadores

5. **GET /api/v1/email-to-ftp/test-connections**
   - Testar conexões com email e FTP
   - Valida conectividade

6. **GET /api/v1/email-to-ftp/status**
   - Obter status atual do processamento
   - Informações em tempo real

7. **POST /api/v1/email-to-ftp/force-reset**
   - Forçar reset do processamento
   - Em caso de travamento

8. **GET /api/v1/email-to-ftp/scheduling**
   - Obter informações de agendamento
   - Retorna dados sobre última execução e próxima execução

### Recursos do Swagger UI

- **Try it out**: Execute APIs diretamente pela interface
- **Schemas**: Visualize modelos de dados
- **Responses**: Veja exemplos de respostas
- **Authentication**: Configure autenticação se necessário

## Configurações

As seguintes configurações foram adicionadas no `application.properties`:

```properties
# Configurações do SpringDoc OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
```

## Dependências Adicionadas

Foi adicionada a seguinte dependência no `pom.xml`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

## Personalização

A documentação pode ser personalizada através da classe `SwaggerConfig.java` localizada em:
`src/main/java/br/com/consultdg/email_to_ftp_service/config/SwaggerConfig.java`

## Modelos Documentados

### ProcessingStats
- `processedEmails`: Número total de emails processados
- `processedAttachments`: Número total de anexos processados  
- `failedUploads`: Número de uploads que falharam

### SchedulingInfo
- `lastExecutionTime`: Timestamp da última execução (0 se nunca executou)
- `lastExecutionDuration`: Duração da última execução em milissegundos
- `nextExecutionTime`: Timestamp estimado da próxima execução
- `processingInterval`: Intervalo configurado entre processamentos (em ms)
- `currentlyProcessing`: Se está processando emails no momento atual
- `currentProcessingTime`: Tempo atual de processamento (se estiver executando)

## Endpoint de Agendamento - Detalhes

### 📅 GET /api/v1/email-to-ftp/scheduling

Este endpoint é especialmente útil para **monitoramento em tempo real** e criação de dashboards.

#### Exemplo de Resposta
```json
{
  "lastExecutionTime": 1752771570829,
  "lastExecutionDuration": 3763,
  "nextExecutionTime": 1752771630829,
  "processingInterval": 60000,
  "currentProcessingTime": 0,
  "currentlyProcessing": false
}
```

#### Casos de Uso para Frontend
1. **Dashboard de Status**: Mostra quando foi a última execução e quando será a próxima
2. **Alertas de Atraso**: Se `nextExecutionTime` < tempo atual, há um atraso
3. **Monitoramento de Performance**: Acompanhar `lastExecutionDuration` ao longo do tempo
4. **Detecção de Travamento**: Se `currentlyProcessing` for true por muito tempo

#### Exemplo JavaScript para Dashboard
```javascript
async function getSchedulingInfo() {
    try {
        const response = await fetch('/api/v1/email-to-ftp/scheduling');
        const data = await response.json();
        
        // Converter timestamps para datas legíveis
        const lastExecution = new Date(data.lastExecutionTime);
        const nextExecution = new Date(data.nextExecutionTime);
        
        console.log('Última execução:', lastExecution.toLocaleString());
        console.log('Próxima execução:', nextExecution.toLocaleString());
        console.log('Duração da última execução:', data.lastExecutionDuration + 'ms');
        console.log('Está processando agora:', data.currentlyProcessing);
        
        return data;
    } catch (error) {
        console.error('Erro ao obter informações de agendamento:', error);
    }
}

// Chamar a cada 30 segundos para monitoramento em tempo real
setInterval(getSchedulingInfo, 30000);
```

#### Interpretação dos Dados
- **Status do Processamento**:
  - `currentlyProcessing: false`: O serviço não está processando emails no momento
  - `currentlyProcessing: true`: O serviço está processando emails agora
  - `currentProcessingTime`: Mostra há quanto tempo está processando (se aplicável)

- **Cálculo do Próximo Processamento**:
  - `nextExecutionTime` = `lastExecutionTime` + `processingInterval`
  - Se `lastExecutionTime` for 0 (nunca executou), próxima execução será em breve

## Suporte

Para dúvidas ou problemas com a documentação Swagger, entre em contato:
- Email: servicesconsultdg@gmail.com
- Site: https://consultdg.com.br
