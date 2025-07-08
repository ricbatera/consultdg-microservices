package br.com.consultdg.chatgpt_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients
@Import(br.com.consultdg.database_mysql_service.config.DatabaseMysqlConfig.class)
public class ChatgptServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatgptServiceApplication.class, args);
	}

}
