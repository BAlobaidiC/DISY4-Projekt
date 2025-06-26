package fhtw.at.energy_community.producer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Spring-Konfiguration für RabbitMQ
public class RabbitMQConfig {

    @Bean
    public Queue queue() {
        return new Queue("energy.queue", false); // Erstellt eine Queue mit Namen "energy.queue", nicht persistent
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(); // Wandelt Java-Objekte in JSON und umgekehrt
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory); // Verbindung zu RabbitMQ
        template.setMessageConverter(messageConverter); // Nutzt JSON als Nachrichtenformat
        return template;
    }
}
