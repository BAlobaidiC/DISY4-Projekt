/**
 * Repository-Interface für die Verwaltung von UsageHour-Entitäten.
 * Bietet Datenbankzugriffsmethoden für Energieverbrauchsdaten.
 *
 * Dieses Interface erweitert JpaRepository und ermöglicht dadurch
 * den Zugriff auf CRUD-Operationen (Create, Read, Update, Delete),
 * ohne dass SQL oder eigene Implementierungen nötig sind.
 */
package fhtw.wien.energy_community_percentage_service.repo;

import fhtw.wien.energy_community_percentage_service.model.UsageHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

/**
 * Spring Data JPA Repository für UsageHour-Entitäten.
 *
 * @extends JpaRepository<UsageHour, LocalDateTime>
 *   - UsageHour: Entity-Klasse, die eine Zeile in der Verbrauchstabelle repräsentiert.
 *   - LocalDateTime: Typ des Primärschlüssels (z. B. die Stunde, für die Verbrauchsdaten gespeichert werden).
 *
 * Durch die Erweiterung von JpaRepository sind u. a. folgende Methoden ohne weitere Implementierung verfügbar:
 * - findById(LocalDateTime): Holt Verbrauchsdaten für eine bestimmte Stunde
 * - save(UsageHour): Speichert neue oder aktualisierte Verbrauchseinträge
 * - delete(UsageHour): Löscht Verbrauchseinträge
 * - findAll(): Listet alle Verbrauchseinträge auf
 * - count(): Gibt die Gesamtanzahl aller Einträge zurück
 * - existsById(LocalDateTime): Prüft, ob Daten für eine bestimmte Stunde existieren
 */
public interface UsageHourRepository extends JpaRepository<UsageHour, LocalDateTime> {
    // Zusätzliche benutzerdefinierte Abfragen können hier ergänzt werden, z. B.:
    // List<UsageHour> findByCommunityId(Long communityId);
}
