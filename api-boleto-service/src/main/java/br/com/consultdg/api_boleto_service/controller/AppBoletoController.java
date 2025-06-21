package br.com.consultdg.api_boleto_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.consultdg.api_boleto_service.proxy.ProtocoloServiceProxy;
import br.com.consultdg.protocolo_service_util.dto.request.ProtocoloRequest;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/boletos")
public class AppBoletoController {
    Logger logger = Logger.getLogger(AppBoletoController.class.getName());

    @Autowired
    private ProtocoloServiceProxy protocoloServiceProxy;

    @GetMapping("get-url-to-upload-s3")
    public String getMethodName(@RequestParam String fileName) {
        logger.info("Novo boleto recebido, destino S3: " + fileName);
        ProtocoloRequest protocoloRequest = new ProtocoloRequest("API_BOLETO_SERVICE");
        var protocoloResponse = protocoloServiceProxy.newProtocol(protocoloRequest);
        return new String("em desenvolvimento, aguarde... " + fileName);
    }
    

}
