package via.pro3.slaughterhouse.dto;

import java.util.ArrayList;
import java.util.List;

    // All DTOs used by Station 2 (cutting + trays).

public class ButcheringDtos {

        // ---------- Tray DTOs ----------
    // ---------------- CREATE Tray ----------------

    public static class CreateTrayDto {
        private String type;      // e.g. "rib", "leg"
        private double maxWeight; // kg

        public CreateTrayDto() {}

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
    }
    // ---------- READ ONE ----------
    // Tray details including current weight and part IDs.

    public static class TrayDto {
        private Long id;
        private String type;
        private double maxWeight;
        private double currentWeight;
        private List<Long> partIds = new ArrayList<>();

        public TrayDto() {}

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

        public double getCurrentWeight() {
            return currentWeight;
        }

        public void setCurrentWeight(double currentWeight) {
            this.currentWeight = currentWeight;
        }

        public List<Long> getPartIds() {
            return partIds;
        }

        public void setPartIds(List<Long> partIds) {
            this.partIds = partIds;
        }
    }

    // ---------------- Put/ Update ----------------
    public static class UpdateTrayDto {
        private String type;
        private Double maxWeight; // nullable: if null, do not change

        public UpdateTrayDto() {}

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Double getMaxWeight() {
            return maxWeight;
        }

        public void setMaxWeight(Double maxWeight) {
            this.maxWeight = maxWeight;
        }
    }

    // ---------------- Get / List of Tray ----------------
    public static class TrayListDto {
        private List<TrayDto> trays = new ArrayList<>();

        public TrayListDto() {}

        public List<TrayDto> getTrays() {
            return trays;
        }

        public void setTrays(List<TrayDto> trays) {
            this.trays = trays;
        }
    }

    // ---------------- PART DTOs ----------------
    // ---------------- Post / Create Part ----------------
    public static class CreatePartDto {
        private double weight; // kg
        private String type;   // e.g. "rib", "leg"
        private String animalRegistrationNumber;
        private Long trayId;   // optional: place directly on a tray

        public CreatePartDto() {}

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

        public String getAnimalRegistrationNumber() {
            return animalRegistrationNumber;
        }

        public void setAnimalRegistrationNumber(String animalRegistrationNumber) {
            this.animalRegistrationNumber = animalRegistrationNumber;
        }

        public Long getTrayId() {
            return trayId;
        }

        public void setTrayId(Long trayId) {
            this.trayId = trayId;
        }
    }

    /**
     * Part details including animal reg. no. and tray ID.
     */
    public static class PartDto {
        private Long id;
        private double weight;
        private String type;
        private String animalRegistrationNumber;
        private Long trayId;

        public PartDto() {}

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

        public String getAnimalRegistrationNumber() {
            return animalRegistrationNumber;
        }

        public void setAnimalRegistrationNumber(String animalRegistrationNumber) {
            this.animalRegistrationNumber = animalRegistrationNumber;
        }

        public Long getTrayId() {
            return trayId;
        }

        public void setTrayId(Long trayId) {
            this.trayId = trayId;
        }
    }

    // ---------------- Put / Update Part ----------------
    public static class UpdatePartDto {
        private Double weight; // nullable
        private String type;   // nullable
        private String animalRegistrationNumber; // nullable
        private Long trayId;   // nullable (change tray or remove if null)

        public UpdatePartDto() {}

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAnimalRegistrationNumber() {
            return animalRegistrationNumber;
        }

        public void setAnimalRegistrationNumber(String animalRegistrationNumber) {
            this.animalRegistrationNumber = animalRegistrationNumber;
        }

        public Long getTrayId() {
            return trayId;
        }

        public void setTrayId(Long trayId) {
            this.trayId = trayId;
        }
    }


    // ---------------- Get / Get List of Part ----------------
    public static class PartListDto {
        private List<PartDto> parts = new ArrayList<>();

        public PartListDto() {}

        public List<PartDto> getParts() {
            return parts;
        }

        public void setParts(List<PartDto> parts) {
            this.parts = parts;
        }
    }
}
