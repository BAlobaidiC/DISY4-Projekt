package at.technikum_wien.gui.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter


public class CurrentPercentage {


    private LocalDateTime hour;

    private double communityDepleted; // Always 100.0
    private double gridPortion;       // (grid_used / total_used) * 100

}