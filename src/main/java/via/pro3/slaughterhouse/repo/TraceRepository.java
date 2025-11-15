package via.pro3.slaughterhouse.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import via.pro3.slaughterhouse.domain.Product;

import java.util.List;
import java.util.UUID;

/**
 * Read-side repository for traceability / recall.
 * Based on Product, but exposes cross-entity JPQL queries.
 */
public interface TraceRepository extends JpaRepository<Product, UUID> {

    // 1) productId -> all animal registration numbers involved in that product
    @Query("""
      select distinct a.registrationNumber
      from Product p
      join p.parts prt
      join prt.animal a
      where p.id = :productId
      """)
    List<String> findAnimalRegistrationNumbersByProductId(UUID productId);

    // 2) registrationNumber -> all product IDs that contain parts from that animal
    @Query("""
      select distinct p.id
      from Product p
      join p.parts prt
      join prt.animal a
      where a.registrationNumber = :registrationNumber
      """)
    List<UUID> findProductIdsByAnimalRegistrationNumber(String registrationNumber);
}
