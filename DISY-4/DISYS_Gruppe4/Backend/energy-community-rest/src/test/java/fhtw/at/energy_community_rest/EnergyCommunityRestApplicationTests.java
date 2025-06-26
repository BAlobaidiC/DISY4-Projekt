package fhtw.at.energy_community_rest;

import fhtw.at.energy_community_rest.model.CurrentPercentage;
import fhtw.at.energy_community_rest.model.UsageHour;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EnergyCommunityRestApplicationTests {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testeInfrastrukturKomponenten() {
        assertAll("Infrastruktur-Komponenten",
            () -> assertNotNull(entityManager,
                "EntityManager sollte verfügbar sein"),
            () -> assertNotNull(rabbitTemplate,
                "RabbitTemplate sollte verfügbar sein"),
            () -> assertDoesNotThrow(() -> {
                entityManager.getMetamodel().entity(UsageHour.class);
                entityManager.getMetamodel().entity(CurrentPercentage.class);
            }, "Entity-Mapping sollte korrekt sein")
        );
    }

    @Test
    void testeMessageBrokerKonfiguration() {
        assertAll("Message Broker Konfiguration",
            () -> assertNotNull(rabbitTemplate.getConnectionFactory(),
                "RabbitMQ Verbindung sollte konfiguriert sein"),
            () -> assertDoesNotThrow(() -> 
                rabbitTemplate.getConnectionFactory().createConnection(),
                "RabbitMQ Verbindung sollte herstellbar sein")
        );
    }
}