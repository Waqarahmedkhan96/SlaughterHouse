package via.pro3.slaughterhouse.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ setup for async animal registration, butchering and product packaging.
 */
@Configuration
public class RabbitMqConfig {

    public static final String ANIMAL_REGISTRATION_QUEUE = "animal-registration-queue";
    public static final String TRAY_CREATION_QUEUE = "tray-creation-queue";      // tray queue
    public static final String PART_CREATION_QUEUE = "part-creation-queue";      // part queue
    public static final String PRODUCT_CREATION_QUEUE = "product-creation-queue";// product queue

    // define animal queue
    @Bean
    public Queue animalRegistrationQueue() {
        return new Queue(ANIMAL_REGISTRATION_QUEUE, true); // durable queue
    }

    // define tray queue
    @Bean
    public Queue trayCreationQueue() {
        return new Queue(TRAY_CREATION_QUEUE, true); // durable queue
    }

    // define part queue
    @Bean
    public Queue partCreationQueue() {
        return new Queue(PART_CREATION_QUEUE, true); // durable queue
    }

    // define product queue
    @Bean
    public Queue productCreationQueue() {
        return new Queue(PRODUCT_CREATION_QUEUE, true); // durable queue
    }

    // rabbit template
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
