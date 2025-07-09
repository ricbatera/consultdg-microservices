package br.com.consultdg.processa_boleto_service.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.consultdg.database_mysql_service.repository.ProtocoloRepository;
import br.com.consultdg.processa_boleto_service.config.RabbitMQConfig;
import br.com.consultdg.processa_boleto_service.service.ProcessaBoletoService;
import br.com.consultdg.processa_boleto_service.service.RegistraProtocoloService;
import br.com.consultdg.processa_boleto_service.service.ValidaBoletoService;
import br.com.consultdg.protocolo_service_util.enums.StatusProtocolo;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;

@Component
public class BoletoConsumer {
    Logger logger = LoggerFactory.getLogger(BoletoConsumer.class);
    @Autowired
    private RegistraProtocoloService registraProtocoloService;

    @Autowired
    private ProtocoloRepository protocoloRepository;

    @Autowired
    private ProcessaBoletoService processaBoletoService;

    @Autowired
    private ValidaBoletoService validaBoletoService;

    /**
     * Método que consome mensagens da fila "novo-boleto-queue".
     * Este método é chamado automaticamente quando uma nova mensagem é recebida na
     * fila.
     *
     * @param protocoloId O ID do protocolo do boleto a ser processado.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveProtocoloId(Long protocoloId) {
        logger.info("[RabbitMQ] Protocolo recebido para processamento: {}", protocoloId);
        registraProtocoloService.atualizaProtocolo(protocoloId, StatusProtocolo.EM_ANDAMENTO, null);
        registraProtocoloService.registraEventoProtocolo(null, protocoloId, SubStatusEventosBoleto.EM_ANDAMENTO, TipoEvento.PROCESSA_BOLETO_PROCESSANDO);
        var protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado com ID: " + protocoloId));
        processaBoletoService.processa(protocolo);
        // Aqui você pode chamar o serviço de processamento do boleto usando o
        // protocoloId
    }

    /**
     * Método que consome mensagens da fila "tesseract-boleto-queue-done".
     * Este método é chamado automaticamente quando uma nova mensagem é recebida na
     * fila.
     *
     * @param protocoloId O ID do protocolo do boleto a ser processado pelo Tesseract.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_TESSERACT_DONE)
    public void receiveTesseractDone(Long protocoloId) {
        logger.info("[RabbitMQ] Protocolo recebido como concluído pelo Tesseract: {}", protocoloId);
        validaBoletoService.validaBoleto(protocoloId);
    }

    /**
     * Método que consome mensagens da fila "chatgpt-boleto-queue-done".
     * Este método é chamado automaticamente quando uma nova mensagem é recebida na
     * fila.
     *
     * @param protocoloId O ID do protocolo do boleto a ser processado pelo ChatGPT.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_CHATGPT_DONE)
    public void receiveChatGptDone(Long protocoloId) {
        logger.info("[RabbitMQ] Protocolo recebido como concluído pelo ChatGPT: {}", protocoloId);
        validaBoletoService.validaBoleto(protocoloId);
    }
}
