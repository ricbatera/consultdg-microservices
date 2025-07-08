package br.com.consultdg.processa_boleto_service.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.consultdg.database_mysql_service.repository.ProtocoloRepository;
import br.com.consultdg.processa_boleto_service.service.ProcessaBoletoService;
import br.com.consultdg.processa_boleto_service.service.RegistraProtocoloService;
import br.com.consultdg.protocolo_service_util.dto.response.NovoProtocoloResponse;
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

    /**
     * Método que consome mensagens da fila "novo-boleto-queue".
     * Este método é chamado automaticamente quando uma nova mensagem é recebida na
     * fila.
     *
     * @param protocoloId O ID do protocolo do boleto a ser processado.
     */
    @RabbitListener(queues = "novo-boleto-queue")
    public void receiveProtocoloId(Long protocoloId) {
        logger.info("[RabbitMQ] Protocolo recebido para processamento: {}", protocoloId);
        registraProtocoloService.atualizaProtocolo(protocoloId, StatusProtocolo.EM_ANDAMENTO, null);
        registraProtocoloService.registraEventoProtocolo(null, protocoloId, SubStatusEventosBoleto.EM_ANDAMENTO, TipoEvento.PROCESSA_BOLETO_CRIADO);
        var protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado com ID: " + protocoloId));
        processaBoletoService.processa(protocolo);
        // Aqui você pode chamar o serviço de processamento do boleto usando o
        // protocoloId
    }
}
