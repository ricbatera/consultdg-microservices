package br.com.consultdg.s3_service.controller;

import java.net.URL;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import br.com.consultdg.s3_service.config.S3Config;
import br.com.consultdg.s3_service.responsesTo.UrlResponse;

@CrossOrigin
@RestController
@RequestMapping("/s3")
public class S3controller {

    @Autowired
    private S3Config s3Config;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @GetMapping("/generate-presigned-url")
    public ResponseEntity<UrlResponse> generatePresignedUrl(@RequestParam String fileName) {
        try {
            // Exemplo com Amazon S3 SDK
            Date expiration = new Date(System.currentTimeMillis() + 3600000); // 1 hora de validade

            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName,
                    fileName)
                    .withMethod(HttpMethod.PUT)
                    .withExpiration(expiration);

            URL url = s3Config.amazonS3().generatePresignedUrl(generatePresignedUrlRequest);
            return ResponseEntity.ok(new UrlResponse(url.toString()));
        } catch (Exception e) {
            // alterar esse retorno para quando criamos exceção handling personalizada
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UrlResponse("Erro ao gerar URL pre-signed"));
        }

    }

}
