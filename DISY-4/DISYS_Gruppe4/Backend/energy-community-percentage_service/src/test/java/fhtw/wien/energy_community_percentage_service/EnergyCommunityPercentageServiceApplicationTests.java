package fhtw.wien.energy_community_percentage_service;

import fhtw.wien.energy_community_percentage_service.repo.CurrentPercentageRepository;
import fhtw.wien.energy_community_percentage_service.repo.UsageHourRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EnergyCommunityPercentageServiceApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CurrentPercentageRepository currentPercentageRepository;

    @Autowired
    private UsageHourRepository usageHourRepository;

    @Test
    void testeKomponentenVerfügbarkeit() {
        assertAll("Komponenten Verfügbarkeit",
            () -> assertNotNull(rabbitTemplate,
                "RabbitTemplate sollte verfügbar sein"),
            () -> assertNotNull(currentPercentageRepository,
                "CurrentPercentageRepository sollte verfügbar sein"),
            () -> assertNotNull(usageHourRepository,
                "UsageHourRepository sollte verfügbar sein")
        );
    }

    @Test
    void testeAnwendungskonfiguration() {
        assertAll("Anwendungskonfiguration",
            () -> assertTrue(applicationContext.containsBean("rabbitTemplate"),
                "RabbitTemplate Bean sollte registriert sein"),
            () -> assertTrue(applicationContext.containsBean("entityManagerFactory"),
                "EntityManagerFactory Bean sollte für JPA verfügbar sein"),
            () -> assertNotNull(applicationContext.getEnvironment().getProperty("spring.jpa.hibernate.ddl-auto"),
                "JPA Konfiguration sollte verfügbar sein")
        );
    }
}