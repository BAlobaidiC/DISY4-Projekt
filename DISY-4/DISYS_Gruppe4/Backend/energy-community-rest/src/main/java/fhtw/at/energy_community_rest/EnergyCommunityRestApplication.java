package fhtw.at.energy_community_rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hauptanwendungsklasse für die REST-Schnittstelle der Energy Community.
 * Diese Anwendung stellt REST-Endpunkte bereit, um Energieverbrauchsdaten
 * und -statistiken abzurufen.
 *
 * @SpringBootApplication aktiviert:
 * - Automatische Konfiguration
 * - Komponenten-Scan
 * - Zusätzliche Spring Boot Funktionalitäten
 */
@SpringBootApplication
public class EnergyCommunityRestApplication {

    /**
     * Startet die Spring Boot Anwendung.
     * Initialisiert den Spring-Kontext und startet den eingebetteten Webserver.
     *
     * @param args Kommandozeilenargumente für die Anwendung
     */
    public static void main(String[] args) {
        SpringApplication.run(EnergyCommunityRestApplication.class, args);
    }
}