package fhtw.wien.energy_community_percentage_service.repo;

import fhtw.wien.energy_community_percentage_service.model.UsageHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UsageHourRepository extends JpaRepository<UsageHour, LocalDateTime> {
}