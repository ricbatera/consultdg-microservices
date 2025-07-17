# Use uma imagem oficial do Nginx
FROM nginx:alpine

# Instalar envsubst para substituição de variáveis de ambiente
RUN apk add --no-cache gettext

# Remover configuração padrão do nginx
RUN rm /etc/nginx/conf.d/default.conf

# Copiar configuração customizada do nginx
COPY nginx.conf /etc/nginx/conf.d/

# Copiar arquivos da aplicação
COPY . /usr/share/nginx/html/

# Copiar template de configuração JavaScript
COPY config.template.js /usr/share/nginx/html/

# Copiar script de inicialização
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Expor porta 80
EXPOSE 80

# Usar script personalizado como entrypoint
ENTRYPOINT ["/docker-entrypoint.sh"]

# Comando padrão
CMD ["nginx", "-g", "daemon off;"]
