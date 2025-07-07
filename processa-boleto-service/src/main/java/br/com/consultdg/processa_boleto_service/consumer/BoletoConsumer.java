package br.com.consultdg.processa_boleto_service.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BoletoConsumer {
    @RabbitListener(queues = "novo-boleto-queue")
    public void receiveProtocoloId(Long protocoloId) {
        System.out.println("[RabbitMQ] Protocolo recebido para processamento: " + protocoloId);
        // Aqui você pode chamar o serviço de processamento do boleto usando o protocoloId
    }
}
