package br.com.consultdg.email_to_ftp_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Email to FTP Service API")
                        .description("Serviço para processamento de emails e transferência de anexos via FTP")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ConsultDG")
                                .email("servicesconsultdg@gmail.com")
                                .url("https://consultdg.com.br"))
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.consultdg.com.br")
                                .description("Servidor de Produção")
                ));
    }
}
