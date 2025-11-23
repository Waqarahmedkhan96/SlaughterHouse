package via.pro3.slaughterhouse.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnimalDtos {

    // ---------------- CREATE ----------------
    public static class CreateAnimalDto {
        private String registrationNumber;
        private double weight;
        private LocalDate arrivalDate;
        private String origin;

        public CreateAnimalDto() {}

        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }

        public LocalDate getArrivalDate() { return arrivalDate; }
        public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }

        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
    }

    // ---------------- READ (single) ----------------
    public static class AnimalDto {
        private Long id;
        private String registrationNumber;
        private double weight;
        private LocalDate arrivalDate;
        private String origin;

        public AnimalDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }

        public LocalDate getArrivalDate() { return arrivalDate; }
        public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }

        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
    }

    // ---------------- READ (list) ----------------

    public static class AnimalListDto {
        private List<AnimalDto> animals = new ArrayList<>();

        public AnimalListDto() {}

        public List<AnimalDto> getAnimals() { return animals; }
        public void setAnimals(List<AnimalDto> animals) { this.animals = animals; }
    }

    // ---------------- UPDATE ----------------

    public static class UpdateAnimalDto {
        private Double weight;
        private LocalDate arrivalDate;
        private String origin;

        public UpdateAnimalDto() {}

        public Double getWeight() { return weight; }
        public void setWeight(Double weight) { this.weight = weight; }

        public LocalDate getArrivalDate() { return arrivalDate; }
        public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }

        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
    }


    // ---------------- DELETE ----------------

    public static class DeleteAnimalDto {
        private String registrationNumber;

        public DeleteAnimalDto() {}

        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    }

    // ---------------- QUERY PARAMETERS ----------------

    public static class AnimalQueryParameters {
        private LocalDate arrivalDate;
        private String origin;

        public AnimalQueryParameters() {}

        public LocalDate getArrivalDate() { return arrivalDate; }
        public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }

        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
    }
}
