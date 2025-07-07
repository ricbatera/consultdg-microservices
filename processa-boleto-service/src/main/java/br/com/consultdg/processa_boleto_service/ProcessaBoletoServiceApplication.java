package br.com.consultdg.processa_boleto_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class ProcessaBoletoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessaBoletoServiceApplication.class, args);
	}

}
