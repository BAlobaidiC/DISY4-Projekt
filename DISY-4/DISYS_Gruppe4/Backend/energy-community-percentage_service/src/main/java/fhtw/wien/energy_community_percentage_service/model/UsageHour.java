/**
 * Entity-Klasse zur Repräsentation stündlicher Energieverbrauchsdaten.
 * Speichert Verbrauchswerte für eine Energiegemeinschaft pro Stunde.
 */
package fhtw.wien.energy_community_percentage_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @Entity - Markiert die Klasse als JPA-Entity für die Datenbankpersistenz
 * @Getter @Setter - Lombok-Annotationen für automatische Getter/Setter-Generierung
 */
@Entity
@Getter
@Setter
public class UsageHour {

    /**
     * Primärschlüssel der Entity.
     * Repräsentiert den Zeitpunkt der Messung (auf Stundenbasis).
     * @Id - Markiert das Feld als Primärschlüssel in der Datenbank
     */
    @Id
    private LocalDateTime hour;

    /**
     * Von der Energiegemeinschaft produzierte Energiemenge.
     * Einheit: Kilowattstunden (kWh)
     * Erfasst die Gesamtproduktion aller Gemeinschaftsmitglieder
     */
    private double communityProduced;

    /**
     * Von der Energiegemeinschaft verbrauchte Energiemenge.
     * Einheit: Kilowattstunden (kWh)
     * Zeigt den Verbrauch aus gemeinschaftlich produzierter Energie
     */
    private double communityUsed;

    /**
     * Aus dem öffentlichen Stromnetz bezogene Energiemenge.
     * Einheit: Kilowattstunden (kWh)
     * Erfasst den zusätzlichen Strombezug bei unzureichender Eigenproduktion
     */
    private double gridUsed;


}