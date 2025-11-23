package via.pro3.slaughterhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import via.pro3.slaughterhouse.entity.Product;

/**
 * Basic CRUD repository for Products.
 * Products are used on the read side by the trace queries.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

}
