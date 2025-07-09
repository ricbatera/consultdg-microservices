package br.com.consultdg.tesseract_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
		info =@Info(title = "Tesseract Service", description = "Serviço que fará a leitura OCR dos boletos com Tesseract", version = "v1", 
		contact = @io.swagger.v3.oas.annotations.info.Contact(name = "Ricardo Alves Roberto",email = "ricardo.roberto@rdrtech.com.br"/*, url = "www.rdrtech.com.br"*/)))
public class OpenApiConfig {

}