package fhtw.at.energy_community_rest.repo;

import fhtw.at.energy_community_rest.model.CurrentPercentage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CurrentPercentageRepository extends JpaRepository<CurrentPercentage, LocalDateTime> {
}