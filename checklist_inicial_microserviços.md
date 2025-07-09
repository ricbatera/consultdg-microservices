# Checklist Inicial para Microserviços

## Configurando o OpenFeign no Projeto

1. **Adicione a dependência no `pom.xml`:**
     ```xml
     <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-openfeign</artifactId>
     </dependency>
     ```

2. **Habilite o Feign na aplicação:**
     ```java
     @SpringBootApplication
     @EnableFeignClients
     public class Application {
           public static void main(String[] args) {
                 SpringApplication.run(Application.class, args);
           }
     }
     ```

3. **Crie um pacote proxy com uma interface Feign Client:**
     ```java
     // Exemplo
     @FeignClient(name = "${services.protocolo-service.name}")
     public interface ProtocoloServiceProxy {

          @PostMapping("/protocolo-service/novo-protocolo")
          NovoProtocoloResponse newProtocol(@RequestBody ProtocoloDto entity);

          @PostMapping("/protocolo-service/atualiza-protocolo")
          void atualizaProtocolo(@RequestBody ProtocoloDto entity);

          @GetMapping("/protocolo-service/{id}")
          ProtocoloDto getProtocoloById(@RequestParam Long id);

          @PostMapping("/boletos-protocolos/novo-evento")
          void novoEvento(@RequestBody EventosProtocoloBoletoRequest entity);
     }
     ```

4. **Configure propriedades no `application.properties` (opcional):**
     ```properties
     services.protocolo-service.url=http://localhost:8091 # exemplo
     services.protocolo-service.name=protocolo-service # exemplo
     ```

5. **Utilize o Feign Client nos serviços:**
     ```java
     @Service
     public class ExemploService {
           @Autowired
           private ProtocoloServiceProxy protocoloServiceProxy;

           public Dados buscarDados() {
                 return protocoloServiceProxy.getDados().getBody();
           }
     }
     ```

---

## Configurando Conexão com Banco MySQL

1. **Adicione a dependência do MySQL no `pom.xml`:**
     ```xml
     <dependency>
          <groupId>com.mysql</groupId>
          <artifactId>mysql-connector-j</artifactId>
          <scope>runtime</scope>
     </dependency>
     <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-data-jpa</artifactId>
     </dependency>
     ```

2. **Configure as propriedades de conexão no `application.properties`:**
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/seu_banco
     spring.datasource.username=usuario
     spring.datasource.password=senha
     spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
     ```

3. **Importe a configuração do banco de dados na sua aplicação:**
     ```java
     @SpringBootApplication
     @Import(br.com.consultdg.database_mysql_service.config.DatabaseMysqlConfig.class)
     public class Application {
            public static void main(String[] args) {
                    SpringApplication.run(Application.class, args);
            }
     }
     ```

---

## Configurando Conexão com Eureka

1. **Adicione a dependência do Eureka Client no `pom.xml`:**
     ```xml
     <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
     </dependency>
     ```

2. **Habilite o Eureka Client na aplicação:**
     ```java
     //AINDA NÃO VALIDEI SE PRECISA DISSO - NOS PROJETOS NÃO TEM
     @SpringBootApplication
     @EnableEurekaClient
     public class Application {
             public static void main(String[] args) {
                         SpringApplication.run(Application.class, args);
             }
     }
     ```

3. **Configure as propriedades de conexão no `application.properties`:**
     ```properties
     eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
     ```

---

## Configurando Conexão com RabbitMQ

1. **Adicione as propriedades de conexão no `application.properties`:**
     ```properties
     spring.rabbitmq.host=localhost
     spring.rabbitmq.port=5672
     spring.rabbitmq.username=admin
     spring.rabbitmq.password=admin
     ```

2. **Certifique-se de que o serviço RabbitMQ está em execução e acessível com as credenciais configuradas.**

3. **Inclua a dependência do starter AMQP no `pom.xml` caso ainda não esteja presente:**
     ```xml
     <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-amqp</artifactId>
     </dependency>
     ```
     ---

## Configurando Swagger com Springdoc

1. **Adicione as propriedades personalizadas no `application.properties`:**
     ```properties
     springdoc.api-docs.path=/api-boleto-service/v3/api-docs
     springdoc.swagger-ui.config-url=/api-boleto-service/v3/api-docs/swagger-config
     springdoc.swagger-ui.url=/api-boleto-service/v3/api-docs
     ```

2. **Inclua a dependência do Springdoc OpenAPI no `pom.xml`:**
     ```xml
     <dependency>
          <groupId>org.springdoc</groupId>
          <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
          <version>${springdoc-version}</version>
     </dependency>
     <dependency>
          <groupId>org.springdoc</groupId>
          <artifactId>springdoc-openapi-starter-webmvc-api</artifactId>
          <version>${springdoc-version}</version>
     </dependency>
     ```

3. **Acesse a interface do Swagger UI:**
     ```
     http://localhost:porta/api-boleto-service/swagger-ui.html
     ```

     ---

## Configurando Zipkin para Tracing Distribuído

1. **Inclua as dependências no `pom.xml`:**
     ```xml
     <dependency>
          <groupId>io.micrometer</groupId>
          <artifactId>micrometer-tracing-bridge-brave</artifactId>
     </dependency>
     <dependency>
          <groupId>io.micrometer</groupId>
          <artifactId>micrometer-tracing</artifactId>
     </dependency>
     <dependency>
          <groupId>io.zipkin.reporter2</groupId>
          <artifactId>zipkin-reporter-brave</artifactId>
     </dependency>
     <dependency>
          <groupId>io.zipkin.brave</groupId>
          <artifactId>brave-instrumentation-spring-web</artifactId>
     </dependency>
     ```

2. **Configure as propriedades no `application.properties`:**
     ```properties
     management.tracing.enabled=true
     management.zipkin.enabled=true
     management.zipkin.base-url=http://localhost:9411
     ```

3. **Certifique-se de que o serviço Zipkin está em execução e acessível no endereço configurado.**

4. **Acesse o painel do Zipkin para visualizar os traces:**
     ```
     http://localhost:9411
     ```