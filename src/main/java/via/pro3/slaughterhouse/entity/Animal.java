package via.pro3.slaughterhouse.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Animal is the origin of all parts.
 * Each record represents one slaughtered animal.
 */
@Entity
public class Animal {

    // Primary key, generated UUID (database-safe unique id)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @Column(nullable = false)
    private double weight; // kg at arrival

    @Column(nullable = false)
    private LocalDate arrivalDate;

    private String origin; // farm, supplier, country etc.

    public Animal() {
    }

    public Animal(String registrationNumber, double weight, LocalDate arrivalDate, String origin) {
        this.registrationNumber = registrationNumber;
        this.weight = weight;
        this.arrivalDate = arrivalDate;
        this.origin = origin;
    }

    // --- getters / setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
