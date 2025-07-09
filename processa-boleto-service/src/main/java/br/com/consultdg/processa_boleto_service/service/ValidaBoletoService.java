package br.com.consultdg.processa_boleto_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.consultdg.database_mysql_service.model.boletos.Boleto;
import br.com.consultdg.database_mysql_service.repository.boletos.BoletoRepository;
import br.com.consultdg.protocolo_service_util.enums.StatusProtocolo;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;

@Service
public class ValidaBoletoService {
    Logger logger = LoggerFactory.getLogger(ValidaBoletoService.class);

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private RegistraProtocoloService registraProtocoloService;

    public void validaBoleto(Long protocoloId) {
        try {
            Boleto boleto = boletoRepository.findByProtocoloId(protocoloId)
                    .orElseThrow(() -> new RuntimeException("Boleto não encontrado para o protocolo ID: " + protocoloId));
            //verifica se o boleto já foi processado pelo Tesseract e ChatGPT
            if(boleto.getChatgptFinalizado() && boleto.getTesseractFinalizado()) {
                logger.info("Boleto processado pelo Tesseract e ChatGPT. Validando boleto. Protocolo: {}", protocoloId);
            } else{
                logger.warn("Boleto ainda não processado pelo Tesseract e ChatGPT. Aguarde. Protocolo: {}", protocoloId);
                return;
            }
            
        } catch (RuntimeException e) {
            logger.error("Erro ao validar boleto: {}", e.getMessage());
            registraProtocoloService.registraEventoErroProtocolo(null, protocoloId,SubStatusEventosBoleto.ERRO,TipoEvento.PROCESSA_BOLETO_ERRO,
                    "Erro ao validar boleto: " + e.getMessage());
            registraProtocoloService.atualizaProtocolo(protocoloId, StatusProtocolo.COM_ERRO, "Erro ao validar boleto: " + e.getMessage());
        }
    }

}
