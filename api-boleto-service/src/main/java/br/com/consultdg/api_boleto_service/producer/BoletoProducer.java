package br.com.consultdg.api_boleto_service.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import br.com.consultdg.api_boleto_service.config.RabbitMQConfig;

@Component
public class BoletoProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendProtocoloId(Long protocoloId) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            protocoloId
        );
    }
}
