package via.pro3.slaughterhouse.rabbitmq.Listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import via.pro3.slaughterhouse.dto.rabbitmq.AnimalRegistrationMessageDtos;
import via.pro3.slaughterhouse.entity.Animal;
import via.pro3.slaughterhouse.rabbitmq.RabbitMqConfig;
import via.pro3.slaughterhouse.repository.AnimalRepository;

/**
 * Consumes animal registration messages from RabbitMQ
 * and writes them to the database when possible.
 */
@Component
public class AnimalRegistrationMessageListener {

    private static final Logger log =
            LoggerFactory.getLogger(AnimalRegistrationMessageListener.class);

    private final AnimalRepository animalRepository;

    public AnimalRegistrationMessageListener(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    // consume from queue
    @RabbitListener(queues = RabbitMqConfig.ANIMAL_REGISTRATION_QUEUE)
    public void onMessage(AnimalRegistrationMessageDtos msg) {
        try {
            // build entity
            Animal animal = new Animal();
            animal.setRegistrationNumber(msg.getRegistrationNumber());
            animal.setWeight(msg.getWeight());
            animal.setArrivalDate(msg.getArrivalDate());
            animal.setOrigin(msg.getOriginFarm());

            // save to db
            animalRepository.save(animal);
            log.info("Animal from queue saved: {}", msg.getRegistrationNumber());
        } catch (DataAccessException ex) {
            // db still down: rethrow to requeue
            log.error("DB error while saving queued animal, will be retried", ex);
            throw ex; // causes message to be requeued (depending on config)
        } catch (Exception ex) {
            // unexpected error: log and drop or handle differently
            log.error("Unexpected error processing animal registration message", ex);
        }
    }
}
