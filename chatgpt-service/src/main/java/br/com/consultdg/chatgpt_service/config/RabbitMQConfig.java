package br.com.consultdg.chatgpt_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String EXCHANGE_NAME = "novo-boleto-exchange";
    public static final String ROUTING_KEY_CHATGPT_DONE = "chatgpt-boleto-route-key-done";
    public static final String QUEUE_CHATGPT_DONE = "chatgpt-boleto-queue-done";

    @Bean
    public Queue queueChatGptDone() {
        return new Queue(QUEUE_CHATGPT_DONE, true);
    }

    @Bean
    public Binding bindingChatGptDone(Queue queueChatGptDone, DirectExchange exchange) {
        return BindingBuilder.bind(queueChatGptDone).to(exchange).with(ROUTING_KEY_CHATGPT_DONE);
    }


    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }
}