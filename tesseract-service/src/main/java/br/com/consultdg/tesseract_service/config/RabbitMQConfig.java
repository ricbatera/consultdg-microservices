package br.com.consultdg.tesseract_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_TESSERACT = "tesseract-boleto-queue";
    public static final String EXCHANGE_NAME = "novo-boleto-exchange";
    public static final String ROUTING_KEY_TESSERACT = "tesseract-boleto-route-key";

    @Bean
    public Queue queueTesseract() {
        return new Queue(QUEUE_TESSERACT, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding bindingTesseract(Queue queueTesseract, DirectExchange exchange) {
        return BindingBuilder.bind(queueTesseract).to(exchange).with(ROUTING_KEY_TESSERACT);
    }
}