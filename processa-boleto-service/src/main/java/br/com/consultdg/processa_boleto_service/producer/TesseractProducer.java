package br.com.consultdg.processa_boleto_service.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.consultdg.processa_boleto_service.config.RabbitMQConfig;

@Component
public class TesseractProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendProtocoloId(Long protocoloId) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY_TESSERACT,
            protocoloId
        );
    }
}
