package fhtw.wien.energy_community_percentage_service.repo;

import fhtw.wien.energy_community_percentage_service.model.CurrentPercentage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CurrentPercentageRepository extends JpaRepository<CurrentPercentage, LocalDateTime> {
}
