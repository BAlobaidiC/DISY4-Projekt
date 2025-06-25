package fhtw.at.energy_community_rest.repo;

import fhtw.at.energy_community_rest.model.UsageHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UsageHourRepository extends JpaRepository<UsageHour, LocalDateTime> {

    List<UsageHour> findAllByHourBetween(LocalDateTime start, LocalDateTime end);
    // => Spring will automatically generate the correct SQL query:
    // => SELECT * FROM usage_hour WHERE hour BETWEEN :start AND :end

}