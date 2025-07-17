// Configurações da aplicação - Template para Docker
window.API_CONFIG = {
    // URL base da API - será substituída pela variável de ambiente
    baseUrl: '${API_BASE_URL}',
    
    // Configurações de ambiente
    environment: '${ENVIRONMENT}',
    
    // Intervalos de atualização
    refreshInterval: ${REFRESH_INTERVAL},
    connectionTestInterval: ${CONNECTION_TEST_INTERVAL},
    schedulingUpdateInterval: ${SCHEDULING_UPDATE_INTERVAL}
};

console.log('Configuração Docker carregada para ambiente:', window.API_CONFIG.environment);
console.log('URL da API:', window.API_CONFIG.baseUrl);
