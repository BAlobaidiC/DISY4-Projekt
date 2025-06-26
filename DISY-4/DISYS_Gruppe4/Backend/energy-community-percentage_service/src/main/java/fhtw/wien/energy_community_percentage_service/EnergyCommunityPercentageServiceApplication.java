/**
 * Hauptanwendungsklasse für den Energy Community Percentage Service.
 * Diese Klasse ist der Einstiegspunkt der Anwendung und konfiguriert die grundlegenden Spring-Boot-Funktionen.
 */
package fhtw.wien.energy_community_percentage_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @SpringBootApplication - Hauptannotation für Spring Boot Anwendungen
 * Kombiniert mehrere Annotationen:
 * - @Configuration: Markiert die Klasse als Quelle für Bean-Definitionen
 * - @EnableAutoConfiguration: Aktiviert Spring Boots Auto-Konfiguration
 * - @ComponentScan: Ermöglicht das Scannen nach Spring-Komponenten im Projektpaket
 */
@SpringBootApplication

/**
 * @EnableRabbit - Aktiviert die RabbitMQ-Funktionalität
 * - Ermöglicht die Verwendung von RabbitMQ für asynchrone Nachrichtenverarbeitung
 * - Aktiviert die Erkennung von @RabbitListener-Annotationen
 * - Konfiguriert die notwendige Messaging-Infrastruktur
 */
@EnableRabbit

/**
 * @EnableScheduling - Aktiviert die Unterstützung für geplante Tasks
 * - Ermöglicht die Verwendung von @Scheduled-Annotationen
 * - Erlaubt die Ausführung von zeitgesteuerten Aufgaben
 * - Wichtig für die regelmäßige Berechnung von Energiestatistiken
 */
@EnableScheduling
public class EnergyCommunityPercentageServiceApplication {

    /**
     * Hauptmethode zum Starten der Anwendung
     * - Initialisiert den Spring ApplicationContext
     * - Startet die eingebettete Servlet-Container
     * - Lädt die Anwendungskonfiguration
     * - Initialisiert alle Spring Beans
     *
     * @param args Kommandozeilenargumente für die Anwendung
     */
    public static void main(String[] args) {
        SpringApplication.run(EnergyCommunityPercentageServiceApplication.class, args);
    }
}