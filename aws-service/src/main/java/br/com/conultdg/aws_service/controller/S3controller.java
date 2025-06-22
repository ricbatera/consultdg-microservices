package br.com.conultdg.aws_service.controller;

import java.net.URL;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import br.com.consultdg.protocolo_service_util.enums.StatusProtocolo;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;
import br.com.conultdg.aws_service.config.S3Config;
import br.com.conultdg.aws_service.service.RegistraProtocoloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AWS", description = "Controller para gerenciamento de requisições do AWS S3")
@CrossOrigin
@RestController
@RequestMapping("/s3")
public class S3controller {

    private Logger logger = LoggerFactory.getLogger(S3controller.class);

    @Autowired
    private S3Config s3Config;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.s3.path.recebidos}")
    private String pathRecebidos;

    @Autowired
    private RegistraProtocoloService registraEventoProtocoloService;

    @GetMapping("/generate-presigned-url")
    @Operation(summary = "Obtém a URL para upload de arquivos S3", description = "Gera uma URL pré-assinada para upload de arquivos no S3.")
    public ResponseEntity<String> generatePresignedUrl(@RequestParam String fileName, @RequestParam(required = true) Long protocoloId) {
        logger.info("Gerando URL pre-signed para o arquivo: {}", fileName + " no seguinte caminho: " + pathRecebidos);
        String keyS3 = pathRecebidos + fileName;
        registraEventoProtocoloService.registraEventoProtocolo(fileName, protocoloId, SubStatusEventosBoleto.ABERTO,TipoEvento.GET_URL_S3);
        if (fileName == null || fileName.isEmpty()) {
            return ResponseEntity.badRequest().body("Nome do arquivo não pode ser vazio");
        }
        try {

            // Exemplo com Amazon S3 SDK
            Date expiration = new Date(System.currentTimeMillis() + 3600000); // 1 hora de validade

            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName,
                    keyS3)
                    .withMethod(HttpMethod.PUT)
                    .withExpiration(expiration);

            URL url = s3Config.amazonS3().generatePresignedUrl(generatePresignedUrlRequest);
            registraEventoProtocoloService.registraEventoProtocoloPathS3Alvo(protocoloId, SubStatusEventosBoleto.EM_ANDAMENTO, TipoEvento.GET_URL_S3, keyS3);
            return ResponseEntity.ok(url.toString());
        } catch (Exception e) {
            // alterar esse retorno para quando criamos exceção handling personalizada
            registraEventoProtocoloService.registraEventoProtocolo(fileName, protocoloId, SubStatusEventosBoleto.ERRO, TipoEvento.GET_URL_S3);
            registraEventoProtocoloService.atualizaProtocolo(protocoloId, StatusProtocolo.COM_ERRO, "Erro ao gerar URL pre-signed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gerar URL pre-signed");
        }

    }
    
    @GetMapping("/hello")
	public String hello() {
		return "Hello, AWS S3 Service is running! Controller name: " + this.getClass().getSimpleName();
	}

}
