package br.com.consultdg.email_to_ftp_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EmailToFtpServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailToFtpServiceApplication.class, args);
	}

}
