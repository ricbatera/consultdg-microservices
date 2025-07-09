package br.com.consultdg.tesseract_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients
@Import(br.com.consultdg.database_mysql_service.config.DatabaseMysqlConfig.class)
public class TesseractServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TesseractServiceApplication.class, args);
	}

}
