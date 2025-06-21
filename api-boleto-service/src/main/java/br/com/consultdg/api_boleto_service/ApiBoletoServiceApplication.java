package br.com.consultdg.api_boleto_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiBoletoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiBoletoServiceApplication.class, args);
	}

}
