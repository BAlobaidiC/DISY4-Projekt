package fhtw.at.energy_community_usage_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
// Repräsentiert die Energienutzung einer Stunde
public class UsageHour {
    // Zeitpunkt der Stunde (Primärschlüssel)
    @Id
    private LocalDateTime hour;

    // Von der Gemeinschaft produzierte Energie
    private double communityProduced;
    // Von der Gemeinschaft verbrauchte Energie
    private double communityUsed;
    // Aus dem Netz genutzte Energie
    private double gridUsed;
}
