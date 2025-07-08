package br.com.consultdg.processa_boleto_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableRabbit
@EnableFeignClients
@SpringBootApplication
@Import(br.com.consultdg.database_mysql_service.config.DatabaseMysqlConfig.class)
public class ProcessaBoletoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessaBoletoServiceApplication.class, args);
	}

}
