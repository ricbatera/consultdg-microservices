package br.com.consultdg.chatgpt_service.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.consultdg.chatgpt_service.config.RabbitMQConfig;


@Component
public class ChatGptProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendProtocoloId(Long protocoloId) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY_CHATGPT_DONE,
            protocoloId
        );
    }
}
