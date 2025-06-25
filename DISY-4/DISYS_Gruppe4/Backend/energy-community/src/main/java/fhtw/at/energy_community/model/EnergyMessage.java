package fhtw.at.energy_community.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Repräsentiert eine Energienachricht im System.
 * Diese Klasse dient zur Speicherung von Energieverbrauchsdaten.
 */
@Getter
@Setter
public class EnergyMessage {
    /** Der Typ der Energienachricht */
    private String type;
    
    /** Die zugehörige Verbindung oder Gemeinschaft */
    private String association;
    
    /** Der Energieverbrauch in Kilowattstunden */
    private double kwh;
    
    /** Zeitstempel der Nachricht */
    private LocalDateTime datetime;
}