package via.pro3.slaughterhouse.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Product is the final packed good.
 * It contains a set of Parts that we can trace back to animals.
 */
@Entity
public class Product {

    // Primary key, generated UUID for each product
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String kind; // "Half animal", "Pack of ribs" , SAME_TYPE | HALF_ANIMAL

    // join table product_items(product_id, part_id)
    @ManyToMany
    @JoinTable(
            name = "product_part",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id")
    )
    private Set<Part> parts = new HashSet<>();

    public Product() {
    }

    public Product(String kind) {
        this.kind = kind;
    }

    // --- getters / setters ---

    public UUID getId() {
        return id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Set<Part> getParts() {
        return parts;
    }

    public void setParts(Set<Part> parts) {
        this.parts = parts;
    }
}
