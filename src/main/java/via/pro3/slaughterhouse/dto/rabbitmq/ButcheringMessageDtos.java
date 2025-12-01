package via.pro3.slaughterhouse.dto.rabbitmq;

import java.io.Serializable;

/**
 * Messages for Station 2 (Butchering): trays and parts.
 */
public class ButcheringMessageDtos {

    // ---------- Tray message ----------
    public static class TrayMessageDto implements Serializable {  // send via RabbitMQ

        private static final long serialVersionUID = 1L;          // version id

        private String type;
        private double maxWeight;

        public TrayMessageDto() {
        }

        public TrayMessageDto(String type, double maxWeight) {
            this.type = type;
            this.maxWeight = maxWeight;
        }

        // getters / setters

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

    // ---------- Part message ----------
    public static class PartMessageDto implements Serializable {  // send via RabbitMQ

        private static final long serialVersionUID = 1L;          // version id

        private double weight;
        private String type;
        private String animalRegistrationNumber;
        private Long trayId; // optional tray

        public PartMessageDto() {
        }

        public PartMessageDto(double weight,
                              String type,
                              String animalRegistrationNumber,
                              Long trayId) {
            this.weight = weight;
            this.type = type;
            this.animalRegistrationNumber = animalRegistrationNumber;
            this.trayId = trayId;
        }

        // getters / setters

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
}
