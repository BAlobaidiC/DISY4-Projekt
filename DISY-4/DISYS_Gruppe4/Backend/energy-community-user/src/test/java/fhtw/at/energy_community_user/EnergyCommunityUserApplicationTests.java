package fhtw.at.energy_community_user;

import fhtw.at.energy_community_user.model.EnergyMessage;
import fhtw.at.energy_community_user.user.EnergyUsageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnergyUsageSenderTests {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Captor
    private ArgumentCaptor<EnergyMessage> messageCaptor;

    private EnergyUsageSender energyUsageSender;
    private static final String QUEUE_NAME = "test.queue";

    @BeforeEach
    void setup() {
        energyUsageSender = new EnergyUsageSender(rabbitTemplate);
        ReflectionTestUtils.setField(energyUsageSender, "queueName", QUEUE_NAME);
    }

    @Test
    void testeSendeNachrichtZuHauptverkehrszeit() {
        // Arrange
        LocalDateTime spitzenzeit = LocalDateTime.of(2025, 6, 25, 8, 0);

        // Act
        energyUsageSender.sendUsageMessage();

        // Assert
        verify(rabbitTemplate).convertAndSend(eq(QUEUE_NAME), messageCaptor.capture());
        EnergyMessage gesendeteNachricht = messageCaptor.getValue();

        assertAll(
            () -> assertEquals("USER", gesendeteNachricht.getType()),
            () -> assertEquals("COMMUNITY", gesendeteNachricht.getAssociation()),
            () -> assertNotNull(gesendeteNachricht.getDatetime()),
            () -> assertTrue(gesendeteNachricht.getKwh() > 0,
                "Verbrauchter Strom sollte größer als 0 sein")
        );
    }

    @Test
    void testeNachrichtenFormat() {
        // Act
        energyUsageSender.sendUsageMessage();

        // Assert
        verify(rabbitTemplate).convertAndSend(eq(QUEUE_NAME), messageCaptor.capture());
        EnergyMessage nachricht = messageCaptor.getValue();

        assertAll(
            () -> assertNotNull(nachricht.getType(), 
                "Nachrichtentyp darf nicht null sein"),
            () -> assertNotNull(nachricht.getAssociation(), 
                "Zuordnung darf nicht null sein"),
            () -> assertNotNull(nachricht.getDatetime(), 
                "Zeitstempel darf nicht null sein"),
            () -> assertTrue(nachricht.getKwh() >= 0, 
                "Energieverbrauch muss positiv sein")
        );
    }

    @Test
    void testeVerbrauchsbereich() {
        // Act
        energyUsageSender.sendUsageMessage();

        // Assert
        verify(rabbitTemplate).convertAndSend(eq(QUEUE_NAME), messageCaptor.capture());
        EnergyMessage nachricht = messageCaptor.getValue();

        double verbrauch = nachricht.getKwh();
        LocalDateTime zeit = nachricht.getDatetime();
        int stunde = zeit.getHour();

        if ((stunde >= 7 && stunde <= 9) || (stunde >= 17 && stunde <= 20)) {
            assertTrue(verbrauch >= 0.004 && verbrauch <= 0.006,
                "Verbrauch zur Hauptzeit sollte zwischen 0.004 und 0.006 kWh liegen");
        } else {
            assertTrue(verbrauch >= 0.002 && verbrauch <= 0.004,
                "Verbrauch zur Nebenzeit sollte zwischen 0.002 und 0.004 kWh liegen");
        }
    }
}