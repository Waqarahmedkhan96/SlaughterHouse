package via.pro3.slaughterhouse.rabbitmq.Listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import via.pro3.slaughterhouse.dto.rabbitmq.PackagingMessageDtos;
import via.pro3.slaughterhouse.entity.Part;
import via.pro3.slaughterhouse.entity.Product;
import via.pro3.slaughterhouse.exception.ButcheringExceptions;
import via.pro3.slaughterhouse.exception.PackagingExceptions;
import via.pro3.slaughterhouse.rabbitmq.RabbitMqConfig;
import via.pro3.slaughterhouse.repository.PartRepository;
import via.pro3.slaughterhouse.repository.ProductRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * Consumes product creation messages and writes products to DB.
 */
@Component
public class PackagingMessageListener {

    private static final Logger log =
            LoggerFactory.getLogger(PackagingMessageListener.class);

    private final ProductRepository productRepository;
    private final PartRepository partRepository;

    public PackagingMessageListener(ProductRepository productRepository,
                                    PartRepository partRepository) {
        this.productRepository = productRepository;
        this.partRepository = partRepository;
    }

    // product consumer
    @RabbitListener(queues = RabbitMqConfig.PRODUCT_CREATION_QUEUE)
    public void onMessage(PackagingMessageDtos msg) {
        try {
            Set<Part> parts = new HashSet<>();
            for (Long partId : msg.getPartIds()) {
                Part part = partRepository.findById(partId)
                        .orElseThrow(() -> new ButcheringExceptions.PartNotFoundException(partId));
                parts.add(part);
            }

            Product product = new Product();
            product.setKind(msg.getKind());
            product.setParts(parts);

            if (product.getParts().isEmpty()) {
                throw new PackagingExceptions.InvalidProductCompositionException(
                        "Queued product must contain at least one part");
            }

            productRepository.save(product);
            log.info("Product from queue saved with kind: {}", msg.getKind());
        } catch (DataAccessException ex) {
            log.error("DB error while saving queued product, will be retried", ex);
            throw ex; // requeue message
        } catch (Exception ex) {
            log.error("Unexpected error processing product message", ex);
        }
    }
}
