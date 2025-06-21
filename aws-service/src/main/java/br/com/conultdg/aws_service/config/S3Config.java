package br.com.conultdg.aws_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// import com.amazonaws.auth.AWSStaticCredentialsProvider;
// import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {
    @Bean
    public AmazonS3 amazonS3() {
        // BasicAWSCredentials awsCreds = new BasicAWSCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY);
        return AmazonS3ClientBuilder.standard()
                //.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion("sa-east-1")
                .build();
    }

}