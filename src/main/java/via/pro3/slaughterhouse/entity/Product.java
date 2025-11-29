package via.pro3.slaughterhouse.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;


/**
 * Product is the final packed good.
 * It contains a set of Parts that we can trace back to animals.
 */
@Entity
public class Product {

    // Primary key, generated ID for each product
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // store enum as text ("SAME_TYPE", "HALF_ANIMAL")
    @Column(nullable = false)
    private ProductKind kind;

    // join table product_items(product_id, part_id)
    @ManyToMany
    @JoinTable(
            name = "product_part",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id")
    )
    private Set<Part> parts = new HashSet<>();

    public Product() {}

    public Product(ProductKind kind) {
        this.kind = kind;
    }

    // --- getters / setters ---

    public Long getId() {
        return id;
    }

    public ProductKind getKind() {
        return kind;
    }

    public void setKind(ProductKind kind) {
        this.kind = kind;
    }

    public Set<Part> getParts() {
        return parts;
    }

    public void setParts(Set<Part> parts) {
        this.parts = parts;
    }
}
