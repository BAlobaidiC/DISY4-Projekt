package fhtw.at.energy_community.producer;

import fhtw.at.energy_community.model.EnergyMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component // Spring-Komponente – wird automatisch als Bean registriert
public class EnergySender {

    @Value("${energy.queue}") // Queue-Name aus application.properties wird hier eingefügt
    private String queueName;

    private final RabbitTemplate rabbitTemplate; // Ermöglicht das Senden von Nachrichten an RabbitMQ
    private final Random random = new Random(); // Für Zufallswerte beim Energieverbrauch

    public EnergySender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 5000) // Führt die Methode alle 5 Sekunden aus (nach Beendigung der letzten)
    public void sendEnergyMessage() {
        // Generiert zufällig eine Energieproduktionsmenge zwischen 0.002 und 0.007 kWh
        double kwh = 0.002 + (0.005 * random.nextDouble());

        // Erstellt eine neue Energienachricht mit PRODUCER-Daten
        EnergyMessage message = new EnergyMessage();
        message.setType("PRODUCER");
        message.setAssociation("COMMUNITY");
        message.setKwh(kwh);
        message.setDatetime(LocalDateTime.now());

        // Sendet die Nachricht an die definierte RabbitMQ-Queue
        rabbitTemplate.convertAndSend(queueName, message);
        System.out.println("Produced: " + kwh + " kWh");
    }
}
