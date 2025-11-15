package via.pro3.slaughterhouse.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import via.pro3.slaughterhouse.domain.Tray;

import java.util.UUID;

/**
 * CRUD repository for Trays.
 * Trays group parts of the same type up to a max weight.
 */
public interface TrayRepository extends JpaRepository<Tray, UUID> {
}
