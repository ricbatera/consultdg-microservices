package br.com.conultdg.aws_service.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
		info =@Info(title = "API AWS Service", description = "API do microserviço que se comunica com AWS", version = "v1", 
		contact = @io.swagger.v3.oas.annotations.info.Contact(name = "Ricardo Alves Roberto",email = "ricardo.roberto@rdrtech.com.br"/*, url = "www.rdrtech.com.br"*/)))
public class OpenApiConfig {

}
