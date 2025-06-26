package fhtw.at.energy_community_usage_service.repo;

import fhtw.at.energy_community_usage_service.entity.UsageHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

// Repository für den Zugriff auf die UsageHour-Entitäten in der Datenbank
public interface UsageHourRepository extends JpaRepository<UsageHour, LocalDateTime> {
}
