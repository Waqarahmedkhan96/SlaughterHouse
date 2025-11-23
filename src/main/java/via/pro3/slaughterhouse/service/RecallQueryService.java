// Step 3 – RecallQueryService
//“Java service that knows how to get data for recall from the repo.”
package via.pro3.slaughterhouse.service;

import org.springframework.stereotype.Service;
import via.pro3.slaughterhouse.repository.TraceRepository;

import java.util.List;
import java.util.UUID;

/**
 * Application service for recall/traceability use cases.
 * Hides database/repository details from gRPC or other callers.
 * gRPC will talk to this service instead of Trace repository directly
 */
@Service
public class RecallQueryService {

    private final TraceRepository traceRepository;

    public RecallQueryService(TraceRepository traceRepository) {
        this.traceRepository = traceRepository;
    }

    // product Long -> list of animal registration numbers
    public List<String> getAnimalRegistrationNumbersByProductId(Long productId) {
        return traceRepository.findAnimalRegistrationNumbersByProductId(productId);
    }

    // animal registration number -> list of product Long ids
    public List<Long> getProductIdsByAnimalRegistrationNumber(String registrationNumber) {
        return traceRepository.findProductIdsByAnimalRegistrationNumber(registrationNumber);
    }
}

