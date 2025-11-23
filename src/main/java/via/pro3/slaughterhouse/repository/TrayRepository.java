package via.pro3.slaughterhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import via.pro3.slaughterhouse.entity.Tray;

/**
 * CRUD repository for Trays.
 * Trays group parts of the same type up to a max weight.
 */
public interface TrayRepository extends JpaRepository<Tray, Long> {
}
