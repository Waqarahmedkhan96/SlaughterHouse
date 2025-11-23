package via.pro3.slaughterhouse.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Tray holds parts of the same type with a max weight.
 * Bidirectional relation: Tray <-> Part (optional for navigation).
 */
@Entity
public class Tray {

    // Primary key, generated UUID for this tray
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // must match Part.type for parts placed here

    @Column(nullable = false)
    private double maxWeight; // kg, total capacity

    // Optional: lets you navigate from tray to its parts (read side)
    @OneToMany(mappedBy = "tray")
    private List<Part> parts = new ArrayList<>();

    public Tray() {
    }

    public Tray(String type, double maxWeight) {
        this.type = type;
        this.maxWeight = maxWeight;
    }

    // --- getters / setters ---

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }
}
