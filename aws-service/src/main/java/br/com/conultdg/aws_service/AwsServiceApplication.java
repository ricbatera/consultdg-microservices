package br.com.conultdg.aws_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AwsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsServiceApplication.class, args);
	}

}
