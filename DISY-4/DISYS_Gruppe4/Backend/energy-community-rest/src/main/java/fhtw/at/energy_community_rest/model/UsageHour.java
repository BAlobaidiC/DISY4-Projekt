package fhtw.at.energy_community_rest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entität zur Speicherung der stündlichen Energienutzungsdaten.
 * Erfasst die Energiemengen aus verschiedenen Quellen pro Stunde.
 */
@Getter
@Setter
@Entity
public class UsageHour {

    /**
     * Zeitstempel der Messung (auf Stundenbasis)
     */
    @Id
    private LocalDateTime hour;

    /**
     * Von der Community produzierte Energiemenge in kWh
     */
    private double communityProduced;

    /**
     * Von der Community verbrauchte Energiemenge in kWh
     */
    private double communityUsed;

    /**
     * Aus dem Stromnetz bezogene Energiemenge in kWh
     */
    private double gridUsed;
}