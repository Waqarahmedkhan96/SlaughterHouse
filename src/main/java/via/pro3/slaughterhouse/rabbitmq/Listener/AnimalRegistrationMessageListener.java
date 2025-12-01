package via.pro3.slaughterhouse.rabbitmq.Listener;

import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import via.pro3.slaughterhouse.dto.rabbitmq.AnimalRegistrationMessageDtos;
import via.pro3.slaughterhouse.entity.Animal;
import via.pro3.slaughterhouse.rabbitmq.RabbitMqConfig;
import via.pro3.slaughterhouse.repository.AnimalRepository;

/**
 * Consumes animal registration messages from RabbitMQ
 * and writes them to the database when possible.
 */
@Component // Spring bean
public class AnimalRegistrationMessageListener {

    private static final Logger log =
            LoggerFactory.getLogger(AnimalRegistrationMessageListener.class);

    private final AnimalRepository animalRepository;

    // constructor injection
    public AnimalRegistrationMessageListener(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    // listen to queue
    @RabbitListener(queues = RabbitMqConfig.ANIMAL_REGISTRATION_QUEUE)
    public void onMessage(AnimalRegistrationMessageDtos msg) {
        try {
            // build entity from DTO
            Animal animal = new Animal();
            animal.setRegistrationNumber(msg.getRegistrationNumber());
            animal.setWeight(msg.getWeight());
            animal.setArrivalDate(msg.getArrivalDate());
            animal.setOrigin(msg.getOriginFarm());

            // save to DB
            animalRepository.save(animal);
            log.info("Animal from queue saved: {}", msg.getRegistrationNumber());

        } catch (CannotCreateTransactionException | JDBCConnectionException ex) {
            // DB down → requeue
            log.error("DB unavailable. Requeuing message...", ex);
            throw ex; // rethrow → RabbitMQ retries

        } catch (DataAccessException ex) {
            // JPA/Hibernate DB error → requeue
            log.error("Database access error. Message will be retried...", ex);
            throw ex; // rethrow → RabbitMQ retries

        } catch (Exception ex) {
            // unknown bug → do NOT retry
            log.error("Unexpected error. Dropping message to avoid infinite loop.", ex);
            // no rethrow → message is ACKed and not redelivered
        }
    }
}
