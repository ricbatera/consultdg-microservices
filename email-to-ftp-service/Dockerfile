# ===========================================
# MULTI-STAGE BUILD PARA OTIMIZAÇÃO
# ===========================================

# Stage 1: Build da aplicação
FROM maven:3.9.6-openjdk-17-slim AS build

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Maven primeiro (para cache de layers)
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Baixar dependências (esta layer será cached se o pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src src

# Compilar e empacotar a aplicação
RUN mvn clean package -DskipTests

# ===========================================
# Stage 2: Runtime da aplicação
# ===========================================

FROM openjdk:17-jdk-slim AS runtime

# Instalar utilitários necessários
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Criar usuário não-root para segurança
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Definir diretório de trabalho
WORKDIR /app

# Copiar o JAR da aplicação do stage de build
COPY --from=build /app/target/email-to-ftp-service-*.jar app.jar

# Criar diretório para logs e dar permissões
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

# Mudar para usuário não-root
USER appuser

# Expor a porta da aplicação
EXPOSE 11000

# Variáveis de ambiente padrão
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SERVER_PORT=11000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:11000/actuator/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
