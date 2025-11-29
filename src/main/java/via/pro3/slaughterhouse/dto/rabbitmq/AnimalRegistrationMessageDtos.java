package via.pro3.slaughterhouse.dto.rabbitmq;

import java.time.LocalDate;

/**
 * Message sent to RabbitMQ when DB is down during animal registration.
 */
public class AnimalRegistrationMessageDtos {

    private String registrationNumber;
    private double weight;
    private LocalDate arrivalDate;
    private String originFarm;

    public AnimalRegistrationMessageDtos() {
    }

    public AnimalRegistrationMessageDtos(String registrationNumber,
                                         double weight,
                                         LocalDate arrivalDate,
                                         String originFarm) {
        this.registrationNumber = registrationNumber;
        this.weight = weight;
        this.arrivalDate = arrivalDate;
        this.originFarm = originFarm;
    }

    // getters / setters

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

    public String getOriginFarm() {
        return originFarm;
    }

    public void setOriginFarm(String originFarm) {
        this.originFarm = originFarm;
    }
}
