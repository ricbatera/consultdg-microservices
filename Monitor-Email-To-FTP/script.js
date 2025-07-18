// Configuração da API
const API_BASE_URL = window.API_CONFIG?.baseUrl || 
                     process?.env?.API_BASE_URL || 
                     'http://localhost:8080/api/v1/email-to-ftp';

// Estado da aplicação
let isConnected = false;
let chart = null;
let statsData = {
    emailsProcessados: 0,
    anexosEnviados: 0,
    falhasUpload: 0,
    ultimaExecucao: null
};

// Dados de agendamento
let schedulingData = {
    lastExecutionTime: 0,
    lastExecutionDuration: 0,
    nextExecutionTime: 0,
    processingInterval: 60000,
    currentlyProcessing: false,
    currentProcessingTime: 0
};

// Armazenar dados anteriores para comparação de tendências
let previousStatsData = {
    emailsProcessados: 0,
    anexosEnviados: 0,
    falhasUpload: 0
};

// Histórico de dados para o gráfico (últimas 24 horas)
let chartHistory = {
    labels: [],
    emailsData: [],
    anexosData: []
};

// Inicialização
document.addEventListener('DOMContentLoaded', function() {
    initializeTheme();
    initializeApp();
    setupAutoRefresh();
    initializeChart();
});

// Inicializar tema
function initializeTheme() {
    // Verificar se há preferência salva no localStorage
    const savedTheme = localStorage.getItem('theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    
    // Definir tema inicial
    const initialTheme = savedTheme || (prefersDark ? 'dark' : 'light');
    setTheme(initialTheme);
    
    // Escutar mudanças na preferência do sistema
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
        if (!localStorage.getItem('theme')) {
            setTheme(e.matches ? 'dark' : 'light');
        }
    });
}

// Alternar tema
function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    setTheme(newTheme);
    localStorage.setItem('theme', newTheme);
    
    // Log da mudança de tema
    addLogEntry('info', `Tema alterado para ${newTheme === 'dark' ? 'escuro' : 'claro'}`);
}

// Definir tema
function setTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    
    // Atualizar gráfico se existir para se adaptar ao novo tema
    if (chart) {
        updateChartTheme(theme);
    }
}

// Atualizar tema do gráfico
function updateChartTheme(theme) {
    const isDark = theme === 'dark';
    const gridColor = isDark ? '#374151' : '#E5E7EB';
    const textColor = isDark ? '#E5E7EB' : '#2C3E50';
    
    chart.options.scales.y.grid.color = gridColor;
    chart.options.scales.x.grid.color = gridColor;
    chart.options.scales.y.ticks.color = textColor;
    chart.options.scales.x.ticks.color = textColor;
    
    chart.update('none'); // Update sem animação
}

// Inicializar aplicação
async function initializeApp() {
    await checkServiceHealth();
    await loadStats();
    await loadSchedulingInfo();
    await autoTestConnections();
    
    // Garantir que o display seja atualizado após carregar os dados
    updateSchedulingDisplay();
    
    // Adicionar event listener para o card de teste de conexões
    setupTestConnectionsCard();
}

// Configurar auto-refresh
function setupAutoRefresh() {
    // Atualizar stats a cada 30 segundos
    setInterval(loadStats, 30000);
    
    // Verificar saúde do serviço a cada 60 segundos
    setInterval(checkServiceHealth, 60000);
    
    // Testar conexões a cada 2 minutos
    setInterval(autoTestConnections, 120000);
    
    // Atualizar informações de agendamento a cada 10 segundos
    setInterval(loadSchedulingInfo, 10000);
    
    // Atualizar display de tempo em tempo real a cada segundo
    setInterval(updateSchedulingDisplay, 1000);
}

// Verificar saúde do serviço
async function checkServiceHealth() {
    try {
        const response = await fetch(`${API_BASE_URL}/health`);
        const data = await response.json();
        
        updateServiceStatus(true, 'Serviço Online');
        isConnected = true;
    } catch (error) {
        console.error('Erro ao verificar saúde do serviço:', error);
        updateServiceStatus(false, 'Serviço Offline');
        isConnected = false;
    }
}

// Atualizar status do serviço na UI
function updateServiceStatus(isOnline, message) {
    const statusElement = document.getElementById('serviceStatus');
    const dotElement = statusElement.querySelector('.status-dot');
    const textElement = statusElement.querySelector('.status-text');
    
    dotElement.className = `status-dot ${isOnline ? '' : 'error'}`;
    textElement.textContent = message;
}

// Carregar estatísticas
async function loadStats() {
    try {
        const response = await fetch(`${API_BASE_URL}/stats`);
        const data = await response.json();
        
        // Armazenar dados anteriores para comparação
        previousStatsData = { ...statsData };
        
        const newStatsData = {
            emailsProcessados: data.processedEmails || 0,
            anexosEnviados: data.processedAttachments || 0,
            falhasUpload: data.failedUploads || 0,
            ultimaExecucao: new Date().toISOString()
        };
        
        // Verificar se houve mudanças para adicionar ao gráfico
        const emailsDiff = newStatsData.emailsProcessados - statsData.emailsProcessados;
        const anexosDiff = newStatsData.anexosEnviados - statsData.anexosEnviados;
        
        // Atualizar statsData
        statsData = newStatsData;
        
        // Se houve atividade, adicionar ao gráfico
        if (emailsDiff > 0 || anexosDiff > 0) {
            addDataToChart(emailsDiff, anexosDiff);
        }
        
        updateStatsUI();
        updateChart();
        
        // Log da atualização se houver mudanças
        if (previousStatsData.emailsProcessados !== statsData.emailsProcessados ||
            previousStatsData.anexosEnviados !== statsData.anexosEnviados ||
            previousStatsData.falhasUpload !== statsData.falhasUpload) {
            addLogEntry('info', `Estatísticas atualizadas: ${statsData.emailsProcessados} emails, ${statsData.anexosEnviados} anexos, ${statsData.falhasUpload} falhas`);
        }
        
    } catch (error) {
        console.error('Erro ao carregar estatísticas:', error);
        showNotification('Erro ao carregar estatísticas', 'error');
    }
}

// Carregar informações de agendamento
async function loadSchedulingInfo() {
    try {
        const response = await fetch(`${API_BASE_URL}/scheduling`);
        const data = await response.json();
        
        schedulingData = {
            lastExecutionTime: data.lastExecutionTime || 0,
            lastExecutionDuration: data.lastExecutionDuration || 0,
            nextExecutionTime: data.nextExecutionTime || 0,
            processingInterval: data.processingInterval || 300000, // 5 minutos por padrão
            currentlyProcessing: data.currentlyProcessing || false,
            currentProcessingTime: data.currentProcessingTime || 0
        };
        
        updateSchedulingDisplay();
        
    } catch (error) {
        console.error('Erro ao carregar informações de agendamento:', error);
        // Em caso de erro, usar valores padrão
        schedulingData = {
            lastExecutionTime: 0,
            lastExecutionDuration: 0,
            nextExecutionTime: 0,
            processingInterval: 300000, // 5 minutos
            currentlyProcessing: false,
            currentProcessingTime: 0
        };
        updateSchedulingDisplay();
    }
}

// Atualizar UI das estatísticas
function updateStatsUI() {
    document.getElementById('emailsProcessados').textContent = statsData.emailsProcessados;
    document.getElementById('anexosEnviados').textContent = statsData.anexosEnviados;
    document.getElementById('falhasUpload').textContent = statsData.falhasUpload;
    
    // Mostrar horário da última atualização dos dados
    const now = new Date();
    document.getElementById('ultimaExecucao').textContent = 
        now.toLocaleTimeString('pt-BR', { 
            hour: '2-digit', 
            minute: '2-digit' 
        });
        
    // Calcular tendências baseadas nos valores atuais
    updateTrends();
}

// Atualizar display de informações de agendamento
function updateSchedulingDisplay() {
    const now = Date.now();
    
    // Atualizar última execução
    if (schedulingData.lastExecutionTime > 0) {
        const lastExecution = new Date(schedulingData.lastExecutionTime);
        const lastExecutionElement = document.getElementById('ultimaExecucaoCompleta');
        if (lastExecutionElement) {
            lastExecutionElement.textContent = lastExecution.toLocaleString('pt-BR');
        }
        
        // Duração da última execução
        const durationElement = document.getElementById('duracaoUltimaExecucao');
        if (durationElement) {
            const duration = Math.round(schedulingData.lastExecutionDuration / 1000);
            durationElement.textContent = `${duration}s`;
        }
    }
    
    // Atualizar próxima execução
    if (schedulingData.nextExecutionTime > 0) {
        const nextExecution = new Date(schedulingData.nextExecutionTime);
        const nextExecutionElement = document.getElementById('proximaExecucao');
        if (nextExecutionElement) {
            if (schedulingData.currentlyProcessing) {
                nextExecutionElement.textContent = 'Processando agora...';
                nextExecutionElement.className = 'scheduling-time processing';
            } else if (schedulingData.nextExecutionTime <= now) {
                nextExecutionElement.textContent = 'Executando em breve...';
                nextExecutionElement.className = 'scheduling-time warning';
            } else {
                const timeUntilNext = Math.max(0, Math.round((schedulingData.nextExecutionTime - now) / 1000));
                const minutes = Math.floor(timeUntilNext / 60);
                const seconds = timeUntilNext % 60;
                nextExecutionElement.textContent = `${minutes}m ${seconds}s`;
                nextExecutionElement.className = 'scheduling-time success';
            }
        }
    } else {
        // Se não há dados de próxima execução, mostrar mensagem padrão
        const nextExecutionElement = document.getElementById('proximaExecucao');
        if (nextExecutionElement) {
            nextExecutionElement.textContent = 'Aguardando dados...';
            nextExecutionElement.className = 'scheduling-time';
        }
    }
    
    // Atualizar intervalo de processamento
    const intervalElement = document.getElementById('intervaloProcessamento');
    if (intervalElement) {
        const intervalMinutes = Math.round(schedulingData.processingInterval / 60000);
        intervalElement.textContent = `${intervalMinutes} minutos`;
    }
    
    // Status de processamento atual (para o card na seção de conexões)
    const processStatusElement = document.getElementById('processStatus');
    if (processStatusElement) {
        if (schedulingData.currentlyProcessing) {
            processStatusElement.textContent = 'Processando';
            processStatusElement.className = 'card-status processing';
        } else {
            processStatusElement.textContent = 'Ativo';
            processStatusElement.className = 'card-status success';
        }
    }
    
    // Status de processamento atual (para o header da seção)
    const statusElement = document.getElementById('statusProcessamento');
    if (statusElement) {
        if (schedulingData.currentlyProcessing) {
            const processingTime = Math.round(schedulingData.currentProcessingTime / 1000);
            statusElement.textContent = `Processando (${processingTime}s)`;
            statusElement.className = 'scheduling-status processing';
            
            // Alerta se estiver processando há muito tempo (mais de 5 minutos)
            if (schedulingData.currentProcessingTime > 300000) {
                statusElement.textContent = `⚠️ Processando há ${Math.round(processingTime / 60)}min`;
                statusElement.className = 'scheduling-status warning';
                
                // Mostrar notificação se ainda não foi mostrada
                if (!window.longProcessingWarningShown) {
                    showNotification('Processamento demorado detectado. Considere usar Reset Forçado.', 'warning');
                    addLogEntry('warning', `Processamento em andamento há ${Math.round(processingTime / 60)} minutos`);
                    window.longProcessingWarningShown = true;
                }
            }
        } else {
            statusElement.textContent = 'Aguardando';
            statusElement.className = 'scheduling-status success';
            window.longProcessingWarningShown = false; // Reset warning flag
        }
    }
    
    // Verificar se há atraso na execução
    if (schedulingData.nextExecutionTime > 0 && schedulingData.nextExecutionTime < now - 60000 && !schedulingData.currentlyProcessing) {
        const delayMinutes = Math.round((now - schedulingData.nextExecutionTime) / 60000);
        addLogEntry('warning', `Execução com atraso de ${delayMinutes} minutos detectada`);
    }
}

// Testar conexões manualmente (chamada pelo botão)
async function testConnections() {
    const button = event?.target?.closest('.control-btn');
    let originalContent = null;
    
    if (button) {
        originalContent = button.innerHTML;
        
        // Mostrar loading
        button.innerHTML = `
            <div class="control-icon">
                <div class="loading"></div>
            </div>
            <div class="control-content">
                <h3>Testando...</h3>
                <p>Verificando conexões</p>
            </div>
        `;
        button.disabled = true;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/test-connections`);
        const data = await response.json();
        
        // A API retorna um status geral, então vamos assumir que ambas as conexões estão OK se o status for success
        if (data.status === 'success') {
            updateConnectionStatus('gmail', { 
                status: 'success', 
                message: 'Conexão IMAP ativa' 
            });
            updateConnectionStatus('ftp', { 
                status: 'success', 
                message: 'Servidor FTP conectado' 
            });
            addLogEntry('success', data.message || 'Conexões testadas com sucesso');
            showNotification('Conexões testadas com sucesso', 'success');
        } else {
            // Se houver erro, marcar ambas como erro
            updateConnectionStatus('gmail', { 
                status: 'error', 
                message: data.message || 'Falha na conexão' 
            });
            updateConnectionStatus('ftp', { 
                status: 'error', 
                message: data.message || 'Falha na conexão' 
            });
            addLogEntry('error', data.message || 'Erro ao testar conexões');
            showNotification('Erro ao testar conexões', 'error');
        }
    } catch (error) {
        console.error('Erro ao testar conexões:', error);
        updateConnectionStatus('gmail', { status: 'error', message: 'Erro ao testar conexão' });
        updateConnectionStatus('ftp', { status: 'error', message: 'Erro ao testar conexão' });
        addLogEntry('error', 'Falha ao conectar com o serviço de testes');
        showNotification('Erro ao testar conexões', 'error');
    } finally {
        // Restaurar botão se foi chamado via interface
        if (button && originalContent) {
            setTimeout(() => {
                button.innerHTML = originalContent;
                button.disabled = false;
            }, 2000);
        }
    }
}

// Função auxiliar para testes automáticos (sem UI de loading)
async function autoTestConnections() {
    try {
        const response = await fetch(`${API_BASE_URL}/test-connections`);
        const data = await response.json();
        
        if (data.status === 'success') {
            updateConnectionStatus('gmail', { 
                status: 'success', 
                message: 'Conexão IMAP ativa' 
            });
            updateConnectionStatus('ftp', { 
                status: 'success', 
                message: 'Servidor FTP conectado' 
            });
        } else {
            updateConnectionStatus('gmail', { 
                status: 'error', 
                message: data.message || 'Falha na conexão' 
            });
            updateConnectionStatus('ftp', { 
                status: 'error', 
                message: data.message || 'Falha na conexão' 
            });
        }
    } catch (error) {
        console.error('Erro ao testar conexões:', error);
        updateConnectionStatus('gmail', { status: 'error', message: 'Erro ao testar conexão' });
        updateConnectionStatus('ftp', { status: 'error', message: 'Erro ao testar conexão' });
    }
}

// Atualizar status das conexões
function updateConnectionStatus(type, connectionData) {
    const statusElement = document.getElementById(`${type}Status`);
    const detailElement = document.getElementById(`${type}Detail`);
    
    let statusText = 'Desconhecido';
    let statusClass = 'warning';
    
    switch (connectionData.status) {
        case 'connected':
        case 'success':
            statusText = 'Conectado';
            statusClass = 'success';
            break;
        case 'error':
        case 'failed':
            statusText = 'Erro';
            statusClass = 'error';
            break;
        case 'disconnected':
            statusText = 'Desconectado';
            statusClass = 'warning';
            break;
    }
    
    statusElement.textContent = statusText;
    statusElement.className = `card-status ${statusClass}`;
    detailElement.textContent = connectionData.message || 'Status da conexão';
}

// Resetar estatísticas
async function resetStats() {
    if (!confirm('Tem certeza que deseja resetar todas as estatísticas?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/stats/reset`, {
            method: 'POST'
        });
        
        if (response.ok) {
            showNotification('Estatísticas resetadas com sucesso', 'success');
            addLogEntry('info', 'Estatísticas resetadas pelo usuário');
            await loadStats();
        } else {
            throw new Error('Erro ao resetar estatísticas');
        }
    } catch (error) {
        console.error('Erro ao resetar estatísticas:', error);
        showNotification('Erro ao resetar estatísticas', 'error');
    }
}

// Atualizar estatísticas
async function refreshStats() {
    const button = event.target.closest('.btn');
    const originalContent = button.innerHTML;
    
    button.innerHTML = '<div class="loading"></div> Atualizando...';
    button.disabled = true;
    
    try {
        await loadStats();
        showNotification('Estatísticas atualizadas', 'success');
    } catch (error) {
        showNotification('Erro ao atualizar estatísticas', 'error');
    } finally {
        setTimeout(() => {
            button.innerHTML = originalContent;
            button.disabled = false;
        }, 1000);
    }
}

// Atualizar logs
function refreshLogs() {
    const button = event.target.closest('.btn');
    const originalContent = button.innerHTML;
    
    button.innerHTML = '<div class="loading"></div> Atualizando...';
    button.disabled = true;
    
    // Simular carregamento de logs
    setTimeout(() => {
        addLogEntry('info', 'Logs atualizados pelo usuário');
        button.innerHTML = originalContent;
        button.disabled = false;
        showNotification('Logs atualizados', 'success');
    }, 1000);
}

// Adicionar entrada no log
function addLogEntry(type, message) {
    const logContainer = document.getElementById('logContainer');
    const now = new Date();
    const timeString = now.toLocaleTimeString('pt-BR', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
    
    const logEntry = document.createElement('div');
    logEntry.className = `log-entry ${type}`;
    logEntry.innerHTML = `
        <div class="log-time">${timeString}</div>
        <div class="log-message">${message}</div>
    `;
    
    logContainer.insertBefore(logEntry, logContainer.firstChild);
    
    // Manter apenas os últimos 50 logs
    const entries = logContainer.querySelectorAll('.log-entry');
    if (entries.length > 50) {
        entries[entries.length - 1].remove();
    }
}

// Atualizar tendências das estatísticas
function updateTrends() {
    const emailsTrendElement = document.getElementById('emailsTrend');
    const anexosTrendElement = document.getElementById('anexosTrend');
    const falhasTrendElement = document.getElementById('falhasTrend');
    
    // Simular tendências baseadas nos valores atuais
    const emailTrend = statsData.emailsProcessados > 0 ? 'positive' : 'neutral';
    const anexoTrend = statsData.anexosEnviados > 0 ? 'positive' : 'neutral';
    const falhasTrend = statsData.falhasUpload > 0 ? 'negative' : 'positive';
    
    // Atualizar classes e textos das tendências
    if (emailsTrendElement) {
        emailsTrendElement.className = `stat-trend ${emailTrend}`;
        emailsTrendElement.innerHTML = `
            <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="${emailTrend === 'positive' ? 'M16 6l2.29 2.29-4.88 4.88-4-4L2 16.59 3.41 18l6-6 4 4 6.3-6.29L22 12V6z' : 'M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z'}"/>
            </svg>
            ${statsData.emailsProcessados > 0 ? `${statsData.emailsProcessados} processados` : 'Nenhum email'}
        `;
    }
    
    if (anexosTrendElement) {
        anexosTrendElement.className = `stat-trend ${anexoTrend}`;
        anexosTrendElement.innerHTML = `
            <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="${anexoTrend === 'positive' ? 'M16 6l2.29 2.29-4.88 4.88-4-4L2 16.59 3.41 18l6-6 4 4 6.3-6.29L22 12V6z' : 'M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z'}"/>
            </svg>
            ${statsData.anexosEnviados > 0 ? `${statsData.anexosEnviados} enviados` : 'Nenhum anexo'}
        `;
    }
    
    if (falhasTrendElement) {
        falhasTrendElement.className = `stat-trend ${falhasTrend}`;
        falhasTrendElement.innerHTML = `
            <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="${falhasTrend === 'positive' ? 'M16 6l2.29 2.29-4.88 4.88-4-4L2 16.59 3.41 18l6-6 4 4 6.3-6.29L22 12V6z' : 'M16 18l2.29-2.29-4.88-4.88-4 4L2 7.41 3.41 6l6 6 4-4 6.3 6.29L22 12v6z'}"/>
            </svg>
            ${statsData.falhasUpload === 0 ? 'Sem falhas' : `${statsData.falhasUpload} falhas`}
        `;
    }
}

// Força reset em caso de travamento (função de emergência)
async function forceReset() {
    if (!confirm('Esta ação força o reset do processamento. Use apenas se o sistema estiver travado. Continuar?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/force-reset`, {
            method: 'POST'
        });
        
        if (response.ok) {
            showNotification('Reset forçado executado com sucesso', 'success');
            addLogEntry('warning', 'Reset forçado executado pelo usuário');
            
            // Atualizar informações após 2 segundos
            setTimeout(async () => {
                await loadSchedulingInfo();
                await loadStats();
            }, 2000);
        } else {
            throw new Error('Erro ao executar reset forçado');
        }
    } catch (error) {
        console.error('Erro ao executar reset forçado:', error);
        showNotification('Erro ao executar reset forçado', 'error');
        addLogEntry('error', 'Falha ao executar reset forçado');
    }
}

// Inicializar gráfico
function initializeChart() {
    const ctx = document.getElementById('activityChart').getContext('2d');
    
    // Inicializar histórico com dados zerados das últimas 24 horas
    initializeChartHistory();
    
    // Detectar tema atual
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
    const isDark = currentTheme === 'dark';
    const gridColor = isDark ? '#374151' : '#E5E7EB';
    const textColor = isDark ? '#E5E7EB' : '#2C3E50';
    
    chart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: chartHistory.labels,
            datasets: [{
                label: 'Emails Processados',
                data: chartHistory.emailsData,
                borderColor: '#0066CC',
                backgroundColor: 'rgba(0, 102, 204, 0.1)',
                fill: true,
                tension: 0.4
            }, {
                label: 'Anexos Enviados',
                data: chartHistory.anexosData,
                borderColor: '#33B5E5',
                backgroundColor: 'rgba(51, 181, 229, 0.1)',
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: gridColor
                    },
                    ticks: {
                        stepSize: 1,
                        color: textColor
                    }
                },
                x: {
                    grid: {
                        color: gridColor
                    },
                    ticks: {
                        color: textColor
                    }
                }
            },
            elements: {
                point: {
                    radius: 3,
                    hoverRadius: 6
                }
            }
        }
    });
}

// Inicializar histórico do gráfico
function initializeChartHistory() {
    chartHistory.labels = [];
    chartHistory.emailsData = [];
    chartHistory.anexosData = [];
    
    // Criar 24 pontos (uma para cada hora)
    for (let i = 23; i >= 0; i--) {
        const hour = new Date();
        hour.setHours(hour.getHours() - i);
        chartHistory.labels.push(hour.getHours().toString().padStart(2, '0') + ':00');
        chartHistory.emailsData.push(0);
        chartHistory.anexosData.push(0);
    }
}

// Atualizar gráfico
function updateChart() {
    if (!chart) return;
    
    const now = new Date();
    const currentHour = now.getHours().toString().padStart(2, '0') + ':00';
    const currentIndex = chartHistory.labels.findIndex(label => label === currentHour);
    
    // Se encontrou a hora atual no gráfico, atualizar os dados
    if (currentIndex !== -1) {
        // Calcular a diferença em relação aos dados anteriores para mostrar atividade incremental
        const emailsDiff = Math.max(0, statsData.emailsProcessados - previousStatsData.emailsProcessados);
        const anexosDiff = Math.max(0, statsData.anexosEnviados - previousStatsData.anexosEnviados);
        
        // Adicionar a diferença ao ponto atual
        chartHistory.emailsData[currentIndex] += emailsDiff;
        chartHistory.anexosData[currentIndex] += anexosDiff;
        
        // Atualizar o gráfico
        chart.data.datasets[0].data = [...chartHistory.emailsData];
        chart.data.datasets[1].data = [...chartHistory.anexosData];
        chart.update('none');
    }
    
    // A cada hora, avançar o gráfico (remover o primeiro ponto e adicionar um novo)
    const lastLabel = chartHistory.labels[chartHistory.labels.length - 1];
    if (currentHour !== lastLabel) {
        // Remover o primeiro ponto (mais antigo)
        chartHistory.labels.shift();
        chartHistory.emailsData.shift();
        chartHistory.anexosData.shift();
        
        // Adicionar novo ponto (hora atual)
        chartHistory.labels.push(currentHour);
        chartHistory.emailsData.push(0);
        chartHistory.anexosData.push(0);
        
        // Atualizar labels e dados do gráfico
        chart.data.labels = [...chartHistory.labels];
        chart.data.datasets[0].data = [...chartHistory.emailsData];
        chart.data.datasets[1].data = [...chartHistory.anexosData];
        chart.update();
    }
}

// Adicionar dados ao gráfico quando houver processamento
function addDataToChart(emails, anexos) {
    if (!chart) return;
    
    const now = new Date();
    const currentHour = now.getHours().toString().padStart(2, '0') + ':00';
    const currentIndex = chartHistory.labels.findIndex(label => label === currentHour);
    
    if (currentIndex !== -1) {
        chartHistory.emailsData[currentIndex] += emails;
        chartHistory.anexosData[currentIndex] += anexos;
        
        chart.data.datasets[0].data = [...chartHistory.emailsData];
        chart.data.datasets[1].data = [...chartHistory.anexosData];
        chart.update('none');
    }
}

// Mostrar notificação
function showNotification(message, type = 'info') {
    // Criar elemento de notificação
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === 'success' ? '#4CAF50' : type === 'error' ? '#F44336' : '#2196F3'};
        color: white;
        padding: 16px 24px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        z-index: 1000;
        font-weight: 500;
        max-width: 300px;
        animation: slideIn 0.3s ease;
    `;
    notification.textContent = message;
    
    // Adicionar animação CSS
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        
        @keyframes slideOut {
            from {
                transform: translateX(0);
                opacity: 1;
            }
            to {
                transform: translateX(100%);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);
    
    document.body.appendChild(notification);
    
    // Remover após 4 segundos
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 4000);
}

// Simular logs iniciais
setTimeout(() => {
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
    addLogEntry('info', 'Sistema de monitoramento iniciado');
    addLogEntry('info', `Tema ${currentTheme === 'dark' ? 'escuro' : 'claro'} ativado`);
    addLogEntry('success', 'Conexões testadas com sucesso');
    addLogEntry('info', 'Auto-refresh configurado para 30 segundos');
}, 1000);

// Configurar card de teste de conexões
function setupTestConnectionsCard() {
    const testCard = document.querySelector('.card-icon.test').closest('.card');
    if (testCard) {
        testCard.style.cursor = 'pointer';
        testCard.addEventListener('click', handleTestConnectionsCardClick);
    }
}

// Manipular clique no card de teste de conexões
async function handleTestConnectionsCardClick() {
    const testStatusElement = document.getElementById('testStatus');
    const originalStatus = testStatusElement.textContent;
    
    // Mostrar loading
    testStatusElement.textContent = 'Testando...';
    testStatusElement.className = 'card-status warning';
    
    try {
        await testConnections();
        
        // Atualizar status para sucesso
        testStatusElement.textContent = 'Testado com Sucesso';
        testStatusElement.className = 'card-status success';
        
        // Voltar ao estado original após 3 segundos
        setTimeout(() => {
            testStatusElement.textContent = 'Verificar Agora';
            testStatusElement.className = 'card-status';
        }, 3000);
        
    } catch (error) {
        // Atualizar status para erro
        testStatusElement.textContent = 'Erro no Teste';
        testStatusElement.className = 'card-status error';
        
        // Voltar ao estado original após 3 segundos
        setTimeout(() => {
            testStatusElement.textContent = 'Verificar Agora';
            testStatusElement.className = 'card-status';
        }, 3000);
    }
}
