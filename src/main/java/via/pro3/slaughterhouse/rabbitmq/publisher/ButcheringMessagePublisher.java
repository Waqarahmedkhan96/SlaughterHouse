package via.pro3.slaughterhouse.rabbitmq.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import via.pro3.slaughterhouse.dto.rabbitmq.ButcheringMessageDtos;
import via.pro3.slaughterhouse.dto.rest.ButcheringDtos;
import via.pro3.slaughterhouse.rabbitmq.RabbitMqConfig;

/**
 * Sends tray and part creation messages when DB is unavailable (Station 2).
 */
@Component
public class ButcheringMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public ButcheringMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // tray fallback
    public void sendTrayCreation(ButcheringDtos.CreateTrayDto dto) {
        ButcheringMessageDtos.TrayMessageDto msg =
                new ButcheringMessageDtos.TrayMessageDto(dto.getType(), dto.getMaxWeight());

        rabbitTemplate.convertAndSend(RabbitMqConfig.TRAY_CREATION_QUEUE, msg);
    }

    // part fallback
    public void sendPartCreation(ButcheringDtos.CreatePartDto dto) {
        ButcheringMessageDtos.PartMessageDto msg =
                new ButcheringMessageDtos.PartMessageDto(
                        dto.getWeight(),
                        dto.getType(),
                        dto.getAnimalRegistrationNumber(),
                        dto.getTrayId()
                );

        rabbitTemplate.convertAndSend(RabbitMqConfig.PART_CREATION_QUEUE, msg);
    }
}
