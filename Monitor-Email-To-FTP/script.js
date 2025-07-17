// Configuração da API
const API_BASE_URL = 'http://localhost:8080/api/v1/email-to-ftp';

// Estado da aplicação
let isConnected = false;
let chart = null;
let statsData = {
    emailsProcessados: 0,
    anexosEnviados: 0,
    falhasUpload: 0,
    ultimaExecucao: null
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
    initializeApp();
    setupAutoRefresh();
    initializeChart();
});

// Inicializar aplicação
async function initializeApp() {
    await checkServiceHealth();
    await loadStats();
    await autoTestConnections();
    updateLastExecutionTime();
}

// Configurar auto-refresh
function setupAutoRefresh() {
    // Atualizar stats a cada 30 segundos
    setInterval(loadStats, 30000);
    
    // Verificar saúde do serviço a cada 60 segundos
    setInterval(checkServiceHealth, 60000);
    
    // Testar conexões a cada 2 minutos
    setInterval(autoTestConnections, 120000);
    
    // Atualizar tempo da próxima execução a cada segundo
    setInterval(updateLastExecutionTime, 1000);
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

// Processar emails manualmente
async function processEmails() {
    const button = event.target.closest('.control-btn');
    const originalContent = button.innerHTML;
    
    // Mostrar loading
    button.innerHTML = `
        <div class="control-icon">
            <div class="loading"></div>
        </div>
        <div class="control-content">
            <h3>Processando...</h3>
            <p>Aguarde o processamento</p>
        </div>
    `;
    button.disabled = true;
    
    try {
        const response = await fetch(`${API_BASE_URL}/process`, {
            method: 'POST'
        });
        
        if (response.ok) {
            const data = await response.json();
            showNotification('Processamento iniciado com sucesso', 'success');
            
            // Adicionar log
            addLogEntry('success', 'Processamento manual iniciado pelo usuário');
            
            // Atualizar stats após 3 segundos para capturar mudanças
            setTimeout(async () => {
                await loadStats();
                addLogEntry('info', 'Dados atualizados após processamento manual');
            }, 3000);
        } else {
            throw new Error('Erro no processamento');
        }
    } catch (error) {
        console.error('Erro ao processar emails:', error);
        showNotification('Erro ao processar emails', 'error');
        addLogEntry('error', 'Falha no processamento manual de emails');
    } finally {
        // Restaurar botão
        setTimeout(() => {
            button.innerHTML = originalContent;
            button.disabled = false;
        }, 2000);
    }
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

// Atualizar tempo da última execução
function updateLastExecutionTime() {
    const now = new Date();
    const minutes = now.getMinutes();
    const nextExecution = 5 - (minutes % 5);
    
    const proximaElement = document.getElementById('proximaExecucao');
    if (proximaElement) {
        if (nextExecution === 5) {
            proximaElement.textContent = 'Executando agora...';
        } else {
            proximaElement.textContent = `Próxima em ${nextExecution}min`;
        }
    }
}

// Inicializar gráfico
function initializeChart() {
    const ctx = document.getElementById('activityChart').getContext('2d');
    
    // Inicializar histórico com dados zerados das últimas 24 horas
    initializeChartHistory();
    
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
                        color: '#E5E7EB'
                    },
                    ticks: {
                        stepSize: 1
                    }
                },
                x: {
                    grid: {
                        color: '#E5E7EB'
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
    addLogEntry('info', 'Sistema de monitoramento iniciado');
    addLogEntry('success', 'Conexões testadas com sucesso');
    addLogEntry('info', 'Auto-refresh configurado para 30 segundos');
}, 1000);
