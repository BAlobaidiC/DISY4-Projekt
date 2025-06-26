package fhtw.at.energy_community_usage_service.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// Repräsentiert eine Nachricht mit Energiedaten
public class EnergyMessage {
    private String type;
    private String association;
    private double kwh;
    private LocalDateTime datetime;

}
