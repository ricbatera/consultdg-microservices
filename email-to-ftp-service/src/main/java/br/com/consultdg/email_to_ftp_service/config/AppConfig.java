package br.com.consultdg.email_to_ftp_service.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({EmailProperties.class, FtpProperties.class})
public class AppConfig {
}
