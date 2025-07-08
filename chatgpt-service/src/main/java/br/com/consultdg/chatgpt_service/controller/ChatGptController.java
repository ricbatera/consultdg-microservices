package br.com.consultdg.chatgpt_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.consultdg.chatgpt_service.service.ProcessPdfService;
import br.com.consultdg.chatgpt_service.service.RegistraProtocoloService;
import br.com.consultdg.protocolo_service_util.dto.boletos.BoletoBase64Request;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/chatgpt")
public class ChatGptController {

    @Autowired
    private ProcessPdfService processPdfService;
    @Autowired
    private RegistraProtocoloService registraProtocoloService;

    @PostMapping("search-boleto")
    public String searchBoleto(@RequestBody BoletoBase64Request entity) {
        registraProtocoloService.registraEventoProtocolo(null,entity.idProtocolo(),SubStatusEventosBoleto.EM_ANDAMENTO,TipoEvento.PROCESSA_BOLETO_RECEBIDO_CHATGPT);
        processPdfService.processaPdfBase64(entity);
        return entity.toString();
    }
    

}
