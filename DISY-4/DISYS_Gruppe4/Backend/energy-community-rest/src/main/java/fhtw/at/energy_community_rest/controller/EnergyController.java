package fhtw.at.energy_community_rest.controller;

// Imports für Modelle, Repositories und Spring-spezifische Annotations
import fhtw.at.energy_community_rest.model.CurrentPercentage;
import fhtw.at.energy_community_rest.model.UsageHour;
import fhtw.at.energy_community_rest.repo.CurrentPercentageRepository;
import fhtw.at.energy_community_rest.repo.UsageHourRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST-Controller für das Energie-Community-System.
 * Stellt Endpunkte zum Abrufen aktueller und historischer Verbrauchsdaten bereit.
 */
@RestController
@RequestMapping("/energy") // Alle Endpunkte beginnen mit /energy
public class EnergyController {

    // Repositories für den Datenbankzugriff
    private final CurrentPercentageRepository percentageRepository;
    private final UsageHourRepository usageHourRepository;

    // Konstruktor mit Dependency Injection
    public EnergyController(CurrentPercentageRepository percentageRepository,
                            UsageHourRepository usageHourRepository) {
        this.percentageRepository = percentageRepository;
        this.usageHourRepository = usageHourRepository;
    }

    /**
     * GET /energy/current
     * Gibt die berechneten Prozentwerte (z. B. Netzanteil) für die aktuelle Stunde zurück.
     */
    @GetMapping("/current")
    public ResponseEntity<CurrentPercentage> getCurrentPercentage() {
        // Aktuelle Stunde (auf volle Stunde gerundet)
        LocalDateTime hour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        // Hole Eintrag aus DB, falls vorhanden → gib 200 OK, sonst 404 Not Found
        return percentageRepository.findById(hour)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /energy/historical?start=...&end=...
     * Gibt alle Verbrauchsdaten (UsageHour) zwischen zwei Zeitpunkten zurück.
     */
    @GetMapping("/historical")
    public List<UsageHour> getHistoricalUsage(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        // Fragt Daten im Zeitbereich von der Datenbank ab
        return usageHourRepository.findAllByHourBetween(start, end);
    }
}
