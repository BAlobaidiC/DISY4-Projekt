package fhtw.at.energy_community.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnergyMessage {
    private String type;
    private String association;
    private double kwh;
    private LocalDateTime datetime;

}
