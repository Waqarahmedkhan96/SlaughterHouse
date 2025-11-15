package via.pro3.slaughterhouse.domain;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Part represents one cut piece from an animal.
 * It links back to Animal and is placed on a Tray.
 */
@Entity
public class Part {

    // Primary key, generated UUID for each part
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private double weight; // kg

    @Column(nullable = false)
    private String type; // e.g., rib, leg

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Animal animal; // origin animal

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Tray tray; // which tray the part is on

    public Part() {
    }

    public Part(double weight, String type, Animal animal, Tray tray) {
        this.weight = weight;
        this.type = type;
        this.animal = animal;
        this.tray = tray;
    }

    // --- getters / setters ---

    public UUID getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public Tray getTray() {
        return tray;
    }

    public void setTray(Tray tray) {
        this.tray = tray;
    }
}
