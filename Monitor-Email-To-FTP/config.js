// Configurações da aplicação
window.API_CONFIG = {
    // URL base da API - modifique conforme necessário
    baseUrl: 'http://localhost:8080/api/v1/email-to-ftp',
    
    // Outras configurações podem ser adicionadas aqui
    refreshInterval: 30000, // 30 segundos
    connectionTestInterval: 120000, // 2 minutos
    schedulingUpdateInterval: 10000, // 10 segundos
    
    // Configurações de ambiente
    environment: 'development' // 'development', 'production', 'staging'
};

// Função para detectar ambiente e ajustar configurações automaticamente
(function detectEnvironment() {
    const hostname = window.location.hostname;
    
    if (hostname === 'localhost' || hostname === '127.0.0.1') {
        window.API_CONFIG.environment = 'development';
        // Manter URL padrão para desenvolvimento local
    } else if (hostname.includes('staging') || hostname.includes('test')) {
        window.API_CONFIG.environment = 'staging';
        // Ajustar URL para staging se necessário
        // window.API_CONFIG.baseUrl = 'https://staging-api.exemplo.com/api/v1/email-to-ftp';
    } else {
        window.API_CONFIG.environment = 'production';
        // Ajustar URL para produção
        // window.API_CONFIG.baseUrl = 'https://api.exemplo.com/api/v1/email-to-ftp';
    }
    
    console.log('Ambiente detectado:', window.API_CONFIG.environment);
    console.log('URL da API:', window.API_CONFIG.baseUrl);
})();
