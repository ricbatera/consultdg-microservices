package br.com.consultdg.tesseract_service.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.consultdg.protocolo_service_util.enums.StatusProtocolo;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;
import br.com.consultdg.tesseract_service.config.RabbitMQConfig;
import br.com.consultdg.tesseract_service.service.RegistraProtocoloService;
import br.com.consultdg.tesseract_service.service.TesseractService;



@Component
public class TesseractConsumer {
    Logger logger = LoggerFactory.getLogger(TesseractConsumer.class);

    @Autowired
    private RegistraProtocoloService registraProtocoloService;
    
    @Autowired
    private TesseractService tesseractService;

    /**
     * Método que consome mensagens da fila "novo-boleto-queue".
     * Este método é chamado automaticamente quando uma nova mensagem é recebida na
     * fila.
     *
     * @param protocoloId O ID do protocolo do boleto a ser processado.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_TESSERACT)
    public void receiveProtocoloId(Long protocoloId) {
        logger.info("[RabbitMQ] Protocolo recebido para processamento: {}", protocoloId);
        try {
            registraProtocoloService.registraEventoProtocolo(null,protocoloId,SubStatusEventosBoleto.EM_ANDAMENTO,TipoEvento.TESSERACT_INICIADO);
            tesseractService.processarBoleto(protocoloId);
            logger.info("[RabbitMQ] Protocolo processado com sucesso: {}", protocoloId);
            registraProtocoloService.registraEventoProtocolo(null,protocoloId,SubStatusEventosBoleto.EM_ANDAMENTO,TipoEvento.TESSERACT_FINALIZADO);
        } catch (Exception e) {
            logger.error("[RabbitMQ] Erro ao processar protocolo: {}", protocoloId, e.getMessage());
            registraProtocoloService.registraEventoErroProtocolo(null, protocoloId, SubStatusEventosBoleto.ERRO, TipoEvento.TESSERACT_ERRO, e.getMessage());
            registraProtocoloService.atualizaProtocolo(protocoloId,StatusProtocolo.COM_ERRO,"[tesseract-service] Erro ao processar protocolo: " + e.getMessage());
        }
    }
}