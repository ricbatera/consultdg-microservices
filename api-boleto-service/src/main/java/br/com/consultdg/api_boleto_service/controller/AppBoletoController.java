package br.com.consultdg.api_boleto_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.consultdg.api_boleto_service.proxy.S3ServiceProxy;
import br.com.consultdg.api_boleto_service.service.RegistraProtocoloService;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/boletos")
public class AppBoletoController {
    Logger logger = LoggerFactory.getLogger(AppBoletoController.class);

    @Autowired
    private RegistraProtocoloService registraProtocoloService;

    @Autowired
    private S3ServiceProxy s3ServiceProxy;

    @GetMapping("get-url-to-upload-s3")
    public String getMethodName(@RequestParam String fileName) {
        logger.info("Novo boleto recebido, destino S3: " + fileName);
       
        // Registra o protocolo do boleto
        var response = registraProtocoloService.registraProtocolo();
        registraProtocoloService.registraEventoProtocolo(fileName, response.id(),SubStatusEventosBoleto.CRIADO, TipoEvento.SOLICITACAO_RECEBIDA);
        // Aqui você pode implementar a lógica para gerar a URL de upload para o S3
        String url = s3ServiceProxy.generatePresignedUrl(fileName, response.id()).getBody();
        // Por enquanto, retornamos uma mensagem de desenvolvimento
        return new String("Nome do arquivo Recebido: " + fileName + "\nUrl: " + url);
    }

}
