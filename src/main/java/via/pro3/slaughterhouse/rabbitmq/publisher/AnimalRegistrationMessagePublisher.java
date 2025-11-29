package via.pro3.slaughterhouse.rabbitmq.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import via.pro3.slaughterhouse.dto.rest.AnimalRegistrationDtos;
import via.pro3.slaughterhouse.dto.rabbitmq.AnimalRegistrationMessageDtos;
import via.pro3.slaughterhouse.rabbitmq.RabbitMqConfig;

/**
 * Sends animal registration messages to RabbitMQ when DB is unavailable.
 */
@Component
public class AnimalRegistrationMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public AnimalRegistrationMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // send to queue
    public void sendAnimalRegistration(AnimalRegistrationDtos.CreateAnimalDto dto) {
        AnimalRegistrationMessageDtos msg = new AnimalRegistrationMessageDtos(
                dto.getRegistrationNumber(),
                dto.getWeight(),
                dto.getArrivalDate(),
                dto.getOrigin()
        );

        rabbitTemplate.convertAndSend(RabbitMqConfig.ANIMAL_REGISTRATION_QUEUE, msg);
    }
}
