package br.com.consultdg.protocolo_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
		info =@Info(title = "API Protocolo Service", description = "API para geração de protocolos e rastrear solicitações", version = "v1", 
		contact = @io.swagger.v3.oas.annotations.info.Contact(name = "Ricardo Alves Roberto",email = "ricardo.roberto@rdrtech.com.br"/*, url = "www.rdrtech.com.br"*/)))
public class OpenApiConfig {

}
