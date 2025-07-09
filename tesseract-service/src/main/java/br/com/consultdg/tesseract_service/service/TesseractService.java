package br.com.consultdg.tesseract_service.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TesseractService {
    private Logger logger = LoggerFactory.getLogger(TesseractService.class);

    public void processarBoleto(Long protocoloId) {
        logger.info("[TesseractService] Iniciando processamento do boleto associado ao protocolo: {}", protocoloId);
        // Lógica para processar o boleto
    }

}
