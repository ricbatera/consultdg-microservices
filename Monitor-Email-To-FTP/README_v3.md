# Monitor Email to FTP - Dashboard de Monitoramento v3.0

Interface web moderna para monitoramento do **Email to FTP Service** da ConsultDG. Esta aplicação oferece monitoramento em tempo real do serviço automatizado de processamento de emails, sem necessidade de intervenção manual.

## 🎯 Visão Geral da Nova Versão

A aplicação foi **redesenhada** para focar completamente no **monitoramento automático**, removendo a dependência de execuções manuais. O serviço agora roda de forma totalmente automatizada, e o dashboard fornece visibilidade completa do que está acontecendo.

## ✨ Principais Funcionalidades

### 📊 **Monitoramento em Tempo Real**
- **Status do Serviço**: Verificação automática da saúde do serviço
- **Conexões**: Monitoramento das conexões Gmail IMAP e FTP
- **Estatísticas Live**: Emails processados, anexos enviados, falhas
- **Gráfico de Atividade**: Visualização das últimas 24 horas

### ⏰ **Informações de Agendamento Detalhadas**
- **Última Execução**: Data, hora e duração do último processamento
- **Próxima Execução**: Countdown em tempo real para o próximo processamento
- **Status Atual**: Indica se está processando, aguardando, ou com problemas
- **Intervalo Configurado**: Mostra o intervalo de processamento (ex: 5 minutos)

### 🚨 **Sistema de Alertas Inteligente**
- **Detecção de Travamento**: Alerta quando processamento demora mais que o esperado
- **Atraso de Execução**: Detecta quando execuções estão atrasadas
- **Notificações Visuais**: Sistema de notificações não intrusivo
- **Log Detalhado**: Histórico completo de atividades

### 🛠️ **Controles de Emergência**
- **Reset Forçado**: Para destravar processamento em caso de problemas
- **Teste de Conexões**: Verificação manual das conexões quando necessário
- **Reset de Estatísticas**: Limpeza de dados quando necessário

## 🔄 **Como Funciona**

### Funcionamento Automático
1. **O serviço roda automaticamente** no intervalo configurado (ex: 5 minutos)
2. **Conecta ao Gmail** via IMAP e busca emails não lidos com anexos
3. **Processa todos os anexos** e envia via FTP
4. **Atualiza as estatísticas** automaticamente
5. **O dashboard monitora** e exibe tudo em tempo real

### Monitoramento da Interface
- **Atualização Automática**: Stats a cada 30s, agendamento a cada 10s
- **Verificação de Saúde**: Status do serviço a cada 60s
- **Teste de Conexões**: Verificação automática a cada 2 minutos
- **Display em Tempo Real**: Countdown e status atualizados a cada segundo

## 📱 **Interface do Dashboard**

### 🎛️ **Painel Principal**
```
┌─────────────────────────────────────────────────────┐
│ 🟢 ConsultDG - Monitor Email to FTP Service ONLINE  │
├─────────────────────────────────────────────────────┤
│ Gmail: ✅ Conectado    FTP: ✅ Conectado            │
│ Status: ✅ Aguardando  Processamento: ⏳ 4m 32s    │
└─────────────────────────────────────────────────────┘

┌─────────────────┬─────────────────┬─────────────────┐
│ 📧 Emails       │ 📎 Anexos       │ ❌ Falhas      │
│     15          │     42          │      0          │
│   +5% hoje      │   +12% hoje     │   -100% hoje    │
└─────────────────┴─────────────────┴─────────────────┘
```

### 📈 **Seção de Agendamento**
```
┌─────────────────────────────────────────────────────┐
│ ⏰ MONITORAMENTO DE AGENDAMENTO      🟢 Aguardando   │
├─────────────────────────────────────────────────────┤
│ ✅ Última Execução     ⏰ Próxima Execução          │
│ 17/07/2025 15:42      4m 32s                        │
│ Duração: 3.2s         Intervalo: 5 minutos          │
│                                                     │
│ 🔧 Testar Conexões    [Verificar Agora]           │
└─────────────────────────────────────────────────────┘
```

### 🚨 **Controles de Emergência**
```
┌─────────────────────────────────────────────────────┐
│ ⚠️  CONTROLES DE EMERGÊNCIA    Use apenas em problemas│
├─────────────────────────────────────────────────────┤
│ [🔄 Reset Forçado]    [🗑️ Zerar Estatísticas]      │
└─────────────────────────────────────────────────────┘
```

## 🛡️ **Estados e Alertas**

### ✅ **Estados Normais**
- **🟢 Aguardando**: Serviço funcionando, aguardando próxima execução
- **🔄 Processando**: Atualmente processando emails (duração mostrada)
- **✅ Conectado**: Conexões Gmail e FTP funcionando

### ⚠️ **Estados de Atenção**
- **🟡 Processando há Xmin**: Processamento demorado (>5min)
- **🟡 Atraso Detectado**: Execução atrasada em relação ao agendado
- **🔌 Desconectado**: Problemas de conectividade

### 🚨 **Estados de Problema**
- **🔴 Serviço Offline**: Serviço não responde
- **❌ Erro de Conexão**: Falha no Gmail ou FTP
- **🚫 Travamento**: Processamento travado há muito tempo

## 🔧 **Cenários de Uso**

### 📊 **Monitoramento Diário**
1. **Abra o dashboard** pela manhã
2. **Verifique as estatísticas** do dia anterior
3. **Observe o status** das conexões
4. **Monitore os logs** para anomalias

### 🔍 **Diagnóstico de Problemas**
1. **Status mostra problema?** → Verifique logs detalhados
2. **Processamento travado?** → Use "Reset Forçado"
3. **Conexões falhando?** → Use "Testar Conexões"
4. **Dados inconsistentes?** → Use "Zerar Estatísticas"

### ⚡ **Resposta a Emergências**
1. **Alerta de travamento** → Reset Forçado + Monitore próxima execução
2. **Muitas falhas** → Verifique configurações de conexão
3. **Serviço offline** → Verifique se o backend está rodando

## 🚀 **Tecnologias Utilizadas**

- **Frontend**: HTML5, CSS3, JavaScript Vanilla
- **Gráficos**: Chart.js para visualização de dados
- **Design**: Sistema de design baseado na identidade ConsultDG
- **API**: Integração REST com Spring Boot backend
- **Responsivo**: Layout adaptável para desktop e mobile

## 📋 **Endpoints da API Utilizados**

### 📊 **Monitoramento**
- `GET /api/v1/email-to-ftp/health` - Status do serviço
- `GET /api/v1/email-to-ftp/stats` - Estatísticas de processamento  
- `GET /api/v1/email-to-ftp/scheduling` - **NOVO**: Informações de agendamento
- `GET /api/v1/email-to-ftp/test-connections` - Teste de conexões

### 🛠️ **Controles**
- `POST /api/v1/email-to-ftp/force-reset` - Reset forçado em emergências
- `POST /api/v1/email-to-ftp/stats/reset` - Zerar estatísticas

## 💡 **Melhorias da Nova Versão**

### ❌ **Removido (Não Mais Necessário)**
- ~~Botão "Processar Emails Manualmente"~~ 
- ~~Execução manual de processamento~~
- ~~Dependência de intervenção humana~~

### ✅ **Adicionado (Novos Recursos)**
- **Endpoint `/scheduling`**: Informações detalhadas de agendamento
- **Monitoramento inteligente**: Detecção automática de problemas
- **Sistema de alertas**: Notificações para situações anômalas  
- **Controles de emergência**: Reset forçado para situações críticas
- **Display em tempo real**: Atualização contínua de status
- **Melhor UX**: Interface focada em monitoramento passivo

## 🔧 **Configuração e Execução**

### **Para Desenvolvimento**
```bash
# 1. Clone o repositório
git clone [url-do-repositório]

# 2. Abra o index.html em um servidor web local
# Exemplo com Python:
python -m http.server 8000

# 3. Acesse http://localhost:8000
```

### **Para Produção**
```bash
# 1. Configure um servidor web (nginx, apache, etc.)
# 2. Aponte para os arquivos estáticos
# 3. Configure proxy reverso para a API (localhost:8080)
```

## 📚 **Documentação Adicional**

- **API Documentation**: Veja `assets/api-doc/README.md` para detalhes completos da API
- **Swagger UI**: Acesse `/swagger-ui.html` no backend para documentação interativa
- **Guia de Marca**: Consulte `assets/DG_Guia de marca.pdf` para diretrizes visuais

## 🤝 **Suporte**

- **Email**: servicesconsultdg@gmail.com
- **Site**: https://consultdg.com.br
- **Logs**: Use o painel de logs da aplicação para diagnóstico
- **Emergência**: Use os controles de emergência quando necessário

---

**Versão**: 3.0 (Redesign Automático)  
**Data**: Julho 2025  
**Status**: ✅ Produção - Monitoramento Automático Ativo
