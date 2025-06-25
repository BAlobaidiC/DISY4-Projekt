package fhtw.at.energy_community_usage_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Konfiguration für RabbitMQ
@Configuration
public class RabbitMQConfiguration {

    // Erstellt und konfiguriert die RabbitMQ-Warteschlange
    @Bean
    public Queue queue() {
        return new Queue("energy.queue", false); // name, durable = false
    }

    // Konfiguriert den MessageConverter für JSON-Nachrichten
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Konfiguriert das RabbitTemplate mit ConnectionFactory und MessageConverter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}