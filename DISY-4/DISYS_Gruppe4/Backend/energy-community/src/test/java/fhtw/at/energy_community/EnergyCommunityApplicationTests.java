package fhtw.at.energy_community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EnergyCommunityApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testeSpringKontextLaden() {
        // Überprüft, ob der Spring-Kontext erfolgreich geladen wird
        assertNotNull(applicationContext, 
            "Der Spring-Anwendungskontext sollte erfolgreich geladen werden");
    }

    @Test
    void testeSpringKonfiguration() {
        // Überprüft die grundlegende Spring-Konfiguration
        assertAll("Spring-Konfiguration",
            () -> assertTrue(applicationContext.getBeanDefinitionCount() > 0,
                "Es sollten Beans im Kontext registriert sein"),
            () -> assertNotNull(applicationContext.getEnvironment(),
                "Spring Environment sollte verfügbar sein"),
            () -> assertNotNull(applicationContext.getApplicationName(),
                "Anwendungsname sollte gesetzt sein")
        );
    }
}