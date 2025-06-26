// Paketdeklaration für den Service
package fhtw.wien.energy_community_percentage_service.service;

// Erforderliche Imports für die Funktionalität
import fhtw.wien.energy_community_percentage_service.model.CurrentPercentage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import fhtw.wien.energy_community_percentage_service.repo.CurrentPercentageRepository;
import fhtw.wien.energy_community_percentage_service.repo.UsageHourRepository;
import java.time.LocalDateTime;

/**
 * Service-Klasse zur Berechnung von Energieverbrauchsprozentsätzen.
 * Verarbeitet Daten der Energiegemeinschaft und berechnet verschiedene Kennzahlen.
 */
@Service // Markiert die Klasse als Spring-Service zur automatischen Erkennung
public class PercentageCalculationService {

    /**
     * Repository für den Zugriff auf stündliche Verbrauchsdaten.
     * Wird als final deklariert, da es nach der Initialisierung nicht mehr geändert werden soll.
     */
    private final UsageHourRepository usageHourRepository;

    /**
     * Repository für die Speicherung der berechneten Prozentsätze.
     * Wird als final deklariert, da es nach der Initialisierung nicht mehr geändert werden soll.
     */
    private final CurrentPercentageRepository percentageRepository;

    /**
     * Konstruktor für Dependency Injection der erforderlichen Repositories.
     * Spring nutzt diesen Konstruktor zur automatischen Bereitstellung der Abhängigkeiten.
     *
     * @param usageHourRepository Repository für Verbrauchsdaten
     * @param percentageRepository Repository für Prozentsätze
     */
    public PercentageCalculationService(
            UsageHourRepository usageHourRepository,
            CurrentPercentageRepository percentageRepository
    ) {
        this.usageHourRepository = usageHourRepository;
        this.percentageRepository = percentageRepository;
    }

    /**
     * Berechnet die Energieverbrauchsprozentsätze.
     * Wird automatisch alle 15 Sekunden ausgeführt.
     * Verarbeitet die aktuellen Verbrauchsdaten und speichert die berechneten Prozentsätze.
     */
    @Scheduled(fixedRate = 15000) // Ausführung alle 15 Sekunden
    public void calculate() {
        // Erstellt einen Zeitstempel für die aktuelle Stunde (ohne Minuten/Sekunden)
        LocalDateTime hour = LocalDateTime.now()
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        // Sucht nach Verbrauchsdaten für die aktuelle Stunde und verarbeitet sie, falls vorhanden
        usageHourRepository.findById(hour).ifPresent(usage -> {
            // Extrahiert die relevanten Verbrauchswerte aus den Daten
            double communityProduced = usage.getCommunityProduced(); // Von der Gemeinschaft produzierte Energie
            double communityUsed = usage.getCommunityUsed();        // Von der Gemeinschaft verbrauchte Energie
            double gridUsed = usage.getGridUsed();                  // Aus dem Netz bezogene Energie
            double totalUsed = communityUsed + gridUsed;            // Gesamter Energieverbrauch

            // Berechnung des Prozentsatzes der verbrauchten Gemeinschaftsenergie
            double communityDepleted;
            if (communityProduced == 0) {
                // Wenn keine Energie produziert wurde, setze auf 100%
                communityDepleted = 100.0;
            } else {
                // Berechne den Prozentsatz, maximal 100%
                communityDepleted = Math.min((communityUsed / communityProduced) * 100.0, 100.0);
            }

            // Berechnung des Netzstromanteils am Gesamtverbrauch
            double gridPortion = totalUsed > 0
                    ? (gridUsed / totalUsed) * 100.0  // Wenn Verbrauch vorhanden, berechne den Prozentsatz
                    : 0.0;                            // Wenn kein Verbrauch, setze auf 0%

            // Erstellen eines neuen Objekts für die berechneten Prozentsätze
            CurrentPercentage percentage = new CurrentPercentage();
            percentage.setHour(hour);                           // Speichere den Zeitstempel
            percentage.setCommunityDepleted(communityDepleted); // Speichere den Gemeinschaftsverbrauch
            percentage.setGridPortion(gridPortion);             // Speichere den Netzanteil

            // Speichern der berechneten Werte in der Datenbank
            percentageRepository.save(percentage);

            // Ausgabe der berechneten Werte im Log
            System.out.printf("✅ [%s] Depleted: %.2f%% | Grid: %.2f%%%n",
                    hour, communityDepleted, gridPortion);
        });
    }
}