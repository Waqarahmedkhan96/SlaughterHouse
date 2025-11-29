package via.pro3.slaughterhouse.rabbitmq.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import via.pro3.slaughterhouse.dto.rabbitmq.PackagingMessageDtos;
import via.pro3.slaughterhouse.dto.rest.PackagingDtos;
import via.pro3.slaughterhouse.rabbitmq.RabbitMqConfig;

/**
 * Sends product creation messages when DB is unavailable.
 */
@Component
public class PackagingMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public PackagingMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // product fallback
    public void sendProductCreation(PackagingDtos.CreateProductDto dto) {
        PackagingMessageDtos msg = new PackagingMessageDtos(
                dto.getKind(),
                dto.getPartIds()
        );

        rabbitTemplate.convertAndSend(RabbitMqConfig.PRODUCT_CREATION_QUEUE, msg);
    }
}
