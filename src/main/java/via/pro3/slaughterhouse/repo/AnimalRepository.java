package via.pro3.slaughterhouse.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import via.pro3.slaughterhouse.domain.Animal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for basic Animal CRUD and lookups.
 * Used to find animals by registration, date, and origin.
 */
public interface AnimalRepository extends JpaRepository<Animal, UUID> {

    // Find one animal by unique registration number
    Optional<Animal> findByRegistrationNumber(String registrationNumber);

    // All animals arriving at a specific date
    List<Animal> findAllByArrivalDate(LocalDate date);

    // All animals from a given origin (case-insensitive)
    List<Animal> findAllByOriginIgnoreCase(String origin);
}
