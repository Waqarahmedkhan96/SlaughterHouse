package via.pro3.slaughterhouse.rabbitmq.Listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import via.pro3.slaughterhouse.dto.rabbitmq.ButcheringMessageDtos;
import via.pro3.slaughterhouse.entity.Animal;
import via.pro3.slaughterhouse.entity.Part;
import via.pro3.slaughterhouse.entity.Tray;
import via.pro3.slaughterhouse.exception.AnimalRegistrationExceptions;
import via.pro3.slaughterhouse.exception.ButcheringExceptions;
import via.pro3.slaughterhouse.rabbitmq.RabbitMqConfig;
import via.pro3.slaughterhouse.repository.AnimalRepository;
import via.pro3.slaughterhouse.repository.PartRepository;
import via.pro3.slaughterhouse.repository.TrayRepository;

/**
 * Consumes tray and part creation messages and writes them to DB (Station 2).
 */
@Component
public class ButcheringMessageListener {

    private static final Logger log =
            LoggerFactory.getLogger(ButcheringMessageListener.class);

    private final TrayRepository trayRepository;
    private final PartRepository partRepository;
    private final AnimalRepository animalRepository;

    public ButcheringMessageListener(TrayRepository trayRepository,
                                     PartRepository partRepository,
                                     AnimalRepository animalRepository) {
        this.trayRepository = trayRepository;
        this.partRepository = partRepository;
        this.animalRepository = animalRepository;
    }

    // tray consumer
    @RabbitListener(queues = RabbitMqConfig.TRAY_CREATION_QUEUE)
    public void onTrayMessage(ButcheringMessageDtos.TrayMessageDto msg) {
        try {
            Tray tray = new Tray();
            tray.setType(msg.getType());
            tray.setMaxWeight(msg.getMaxWeight());

            trayRepository.save(tray);
            log.info("Tray from queue saved: type={}, maxWeight={}", msg.getType(), msg.getMaxWeight());
        } catch (DataAccessException ex) {
            log.error("DB error while saving queued tray, will be retried", ex);
            throw ex; // requeue message
        } catch (Exception ex) {
            log.error("Unexpected error processing tray message", ex);
        }
    }

    // part consumer
    @RabbitListener(queues = RabbitMqConfig.PART_CREATION_QUEUE)
    public void onPartMessage(ButcheringMessageDtos.PartMessageDto msg) {
        try {
            Animal animal = animalRepository.findByRegistrationNumber(msg.getAnimalRegistrationNumber())
                    .orElseThrow(() ->
                            new AnimalRegistrationExceptions.AnimalNotFoundException(msg.getAnimalRegistrationNumber()));

            Part part = new Part();
            part.setWeight(msg.getWeight());
            part.setType(msg.getType());
            part.setAnimal(animal);

            if (msg.getTrayId() != null) {
                Tray tray = trayRepository.findById(msg.getTrayId())
                        .orElseThrow(() -> new ButcheringExceptions.TrayNotFoundException(msg.getTrayId()));
                // simple link
                part.setTray(tray);
                tray.getParts().add(part);
            }

            partRepository.save(part);
            log.info("Part from queue saved for animal: {}", msg.getAnimalRegistrationNumber());
        } catch (DataAccessException ex) {
            log.error("DB error while saving queued part, will be retried", ex);
            throw ex; // requeue message
        } catch (Exception ex) {
            log.error("Unexpected error processing part message", ex);
        }
    }
}
