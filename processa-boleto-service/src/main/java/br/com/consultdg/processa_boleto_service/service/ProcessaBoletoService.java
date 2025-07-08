package br.com.consultdg.processa_boleto_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.consultdg.database_mysql_service.model.Protocolo;
import br.com.consultdg.database_mysql_service.repository.boletos.BoletoRepository;
import br.com.consultdg.processa_boleto_service.proxy.ChatGptServiceProxy;
import br.com.consultdg.protocolo_service_util.dto.boletos.BoletoBase64Request;

@Service
public class ProcessaBoletoService {

    Logger logger = LoggerFactory.getLogger(ProcessaBoletoService.class);

    @Autowired
    private ChatGptServiceProxy chatGptBoletoService;

    @Autowired
    private BoletoRepository boletoRepository;

    public void processa(Protocolo protocolo) {
        var boleto = boletoRepository.findById(protocolo.getId())
                .orElseThrow(() -> new RuntimeException("Boleto não encontrado: " + protocolo.getId()));
        try {
            chatGptBoletoService.searchBoleto(new BoletoBase64Request(
                    boleto.getArquivoPdfBase64(),
                    protocolo.getId(),
                    boleto.getNomeArquivo()));
        } catch (Exception e) {
            logger.error("Erro ao processar boleto: {}", e.getMessage(), e);
            //throw new RuntimeException("Erro ao processar boleto: " + e.getMessage(), e);
        }
    }

}
