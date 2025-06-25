package fhtw.at.energy_community;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hauptanwendungsklasse für die Energy Community Anwendung.
 * 
 * @EnableScheduling - Aktiviert die Zeitplanung für geplante Aufgaben
 * @EnableRabbit - Aktiviert die RabbitMQ-Integration für Messaging
 */
@SpringBootApplication
@EnableScheduling
@EnableRabbit
public class EnergyCommunityApplication {

    /**
     * Hauptmethode zum Starten der Spring Boot Anwendung
     * 
     * @param args Kommandozeilenargumente
     */
    public static void main(String[] args) {
        SpringApplication.run(EnergyCommunityApplication.class, args);
    }
}