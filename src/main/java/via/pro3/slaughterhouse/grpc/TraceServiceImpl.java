package via.pro3.slaughterhouse.grpc;

import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;
import via.pro3.slaughterhouse.generated.GetAnimalsByProductRequest;
import via.pro3.slaughterhouse.generated.GetAnimalsByProductResponse;
import via.pro3.slaughterhouse.generated.GetProductsByAnimalRequest;
import via.pro3.slaughterhouse.generated.GetProductsByAnimalResponse;
import via.pro3.slaughterhouse.generated.TraceServiceGrpc;
import via.pro3.slaughterhouse.service.RecallQueryService;

import java.util.List;

/**
 * gRPC server for the TraceService defined in trace_service.proto.
 * Converts protobuf requests into RecallQueryService calls and back to responses.
 */
@GrpcService
public class TraceServiceImpl extends TraceServiceGrpc.TraceServiceImplBase {

    private final RecallQueryService recallService;

    // Spring injects RecallQueryService; no manual wiring needed.
    public TraceServiceImpl(RecallQueryService recallService) {
        this.recallService = recallService;
    }

    @Override
    public void getAnimalsByProduct(
            GetAnimalsByProductRequest req,
            StreamObserver<GetAnimalsByProductResponse> out) {

        String productIdText = req.getProductId();

// 🔹 safe convert String → Long for the service layer
        Long productId;
        try {
            productId = Long.parseLong(productIdText);
        } catch (NumberFormatException e) {
            out.onError(
                    io.grpc.Status.INVALID_ARGUMENT
                            .withDescription("product_id must be a number: " + productIdText)
                            .asRuntimeException()
            );
            return;
        }

        // Service does DB query using Long id
        List<String> regs =
                recallService.getAnimalRegistrationNumbersByProductId(productId);

        GetAnimalsByProductResponse resp = GetAnimalsByProductResponse.newBuilder()
                .addAllAnimalRegistrationNumbers(regs)
                .build();

        out.onNext(resp);
        out.onCompleted();
    }

    @Override
    public void getProductsByAnimal(
            GetProductsByAnimalRequest req,
            StreamObserver<GetProductsByAnimalResponse> out) {

        String animalReg = req.getAnimalRegistrationNumber();

        // 🔹 Validate input: must not be empty "" or blank
        if (animalReg == null || animalReg.isBlank()) {
            out.onError(
                    io.grpc.Status.INVALID_ARGUMENT
                            .withDescription("animal_registration_number cannot be empty")
                            .asRuntimeException()
            );
            return;
        }

        // Database query (List<Long>)
        List<String> productIds = recallService
                .getProductIdsByAnimalRegistrationNumber(animalReg)
                .stream()
                .map(id -> id.toString())
                .toList();

        GetProductsByAnimalResponse resp = GetProductsByAnimalResponse.newBuilder()
                .addAllProductIds(productIds)
                .build();

        out.onNext(resp);
        out.onCompleted();
    }
}
