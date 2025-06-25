package fhtw.at.energy_community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EnergyCommunityApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void testeAnwendungskontextLaden() {
        // Überprüft, ob der Spring-Kontext korrekt geladen wird
        assertNotNull(applicationContext, 
            "Der Anwendungskontext sollte erfolgreich geladen werden");
        assertTrue(applicationContext.getBeanDefinitionCount() > 0,
            "Der Kontext sollte Beans enthalten");
    }

    @Test
    void testeRabbitMQKonfiguration() {
        // Überprüft, ob RabbitMQ korrekt konfiguriert ist
        assertNotNull(rabbitTemplate, 
            "RabbitTemplate sollte verfügbar sein");
        assertNotNull(rabbitTemplate.getConnectionFactory(),
            "RabbitMQ Verbindungsfactory sollte konfiguriert sein");
        assertTrue(applicationContext.containsBean("rabbitTemplate"),
            "RabbitTemplate Bean sollte im Kontext registriert sein");
    }
}