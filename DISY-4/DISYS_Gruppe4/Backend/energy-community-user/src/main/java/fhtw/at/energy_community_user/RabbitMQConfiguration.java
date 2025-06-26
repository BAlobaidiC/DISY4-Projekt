package fhtw.at.energy_community_user;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// Diese Klasse definiert die RabbitMQ-Warteschlange und den MessageConverter für JSON-Nachrichten
// RabbitMQ-Konfiguration für die Energy Community User Anwendung (Spring Konfigurationsklasse, igurationsklasse,
// wird beim Start gescannt)
@Configuration
public class RabbitMQConfiguration {

    @Bean
    public Queue queue() {
        return new Queue("energy.queue", false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
