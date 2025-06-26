/**
 * Repository für CurrentPercentage-Entitäten.
 * Verwaltet stündlich berechnete Verbrauchsprozentsätze.
 */
package fhtw.wien.energy_community_percentage_service.repo;

import fhtw.wien.energy_community_percentage_service.model.CurrentPercentage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

/**
 * Bietet CRUD-Operationen für CurrentPercentage.
 * Entity: CurrentPercentage, Primärschlüssel: LocalDateTime
 *
 * Standardmethoden z. B.:
 * - save(), findById(), findAll(), delete(), count(), existsById()
 */
public interface CurrentPercentageRepository extends JpaRepository<CurrentPercentage, LocalDateTime> {
    // Eigene Abfragen können bei Bedarf ergänzt werden
}
