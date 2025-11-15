package via.pro3.slaughterhouse.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import via.pro3.slaughterhouse.domain.Product;

import java.util.UUID;

/**
 * Basic CRUD repository for Products.
 * Products are used on the read side by the trace queries.
 */
public interface ProductRepository extends JpaRepository<Product, UUID> {

}
