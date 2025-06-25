package at.technikum_wien.gui.model;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class UsageHour {

    public UsageHour() {}

    public UsageHour(LocalDateTime hour, double communityProduced, double communityUsed, double gridUsed) {
        this.hour = hour;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
    }

    private LocalDateTime hour;

    private double communityProduced;
    private double communityUsed;
    private double gridUsed;
}
