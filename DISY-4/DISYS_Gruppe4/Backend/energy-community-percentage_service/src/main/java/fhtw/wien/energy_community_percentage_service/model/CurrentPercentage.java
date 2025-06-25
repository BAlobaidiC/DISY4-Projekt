package fhtw.wien.energy_community_percentage_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter

@Entity
public class CurrentPercentage {

    @Id
    private LocalDateTime hour;

    private double communityDepleted; // Always 100.0
    private double gridPortion;       // (grid_used / total_used) * 100

}

