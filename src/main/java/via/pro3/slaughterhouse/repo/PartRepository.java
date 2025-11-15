package via.pro3.slaughterhouse.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import via.pro3.slaughterhouse.domain.Part;

import java.util.UUID;

/**
 * Repository for basic Part CRUD operations.
 * Write-side: used when station 2 creates and updates parts.
 */
public interface PartRepository extends JpaRepository<Part, UUID> {
}
