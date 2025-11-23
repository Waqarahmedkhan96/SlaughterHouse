package via.pro3.slaughterhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import via.pro3.slaughterhouse.entity.Part;

/**
 * Repository for basic Part CRUD operations.
 * Write-side: used when station 2 creates and updates parts.
 */
public interface PartRepository extends JpaRepository<Part, Long> {
}
