package fhtw.at.energy_community_user.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EnergyMessage {
    private String type;
    private String association;
    private double kwh;
    private LocalDateTime datetime;

}
