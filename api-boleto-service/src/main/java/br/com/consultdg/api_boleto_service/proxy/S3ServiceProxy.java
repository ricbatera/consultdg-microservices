package br.com.consultdg.api_boleto_service.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "${services.s3-service.name}", url = "${services.s3-service.url}")
public interface S3ServiceProxy {

    @GetMapping("/s3/generate-presigned-url")
    public ResponseEntity<String> generatePresignedUrl(@RequestParam String fileName, @RequestParam(required = true) Long protocoloId);

}
