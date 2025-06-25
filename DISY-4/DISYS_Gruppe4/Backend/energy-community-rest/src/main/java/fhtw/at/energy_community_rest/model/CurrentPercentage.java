package fhtw.at.energy_community_rest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entität zur Speicherung der aktuellen Energienutzungsanteile.
 * Speichert für jede Stunde die prozentualen Anteile der Energiequellen.
 */
@Getter
@Setter
@Entity
public class CurrentPercentage {

    /**
     * Zeitstempel der Messung (auf Stundenbasis)
     */
    @Id
    private LocalDateTime hour;

    /**
     * Prozentsatz der von der Community genutzten Energie
     * Ist immer 100%, da alle Energie entweder aus der Community
     * oder aus dem Netz kommt
     */
    private double communityDepleted;

    /**
     * Prozentualer Anteil der aus dem Stromnetz bezogenen Energie
     * Berechnet sich aus: (Netzverbrauch / Gesamtverbrauch) * 100
     */
    private double gridPortion;
}