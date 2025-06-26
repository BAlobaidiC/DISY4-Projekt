package fhtw.at.energy_community.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Diese Klasse beschreibt eine Energienachricht,
 * die vom Producer oder User geschickt wird.
 */
@Getter
@Setter
public class EnergyMessage {

    /** Gibt an, ob die Nachricht vom PRODUCER oder USER kommt */
    private String type;

    /** Name der Gemeinschaft, z. B. "COMMUNITY" */
    private String association;

    /** Menge der erzeugten oder verbrauchten Energie in kWh */
    private double kwh;

    /** Zeitpunkt, an dem die Nachricht erstellt wurde */
    private LocalDateTime datetime;
}
