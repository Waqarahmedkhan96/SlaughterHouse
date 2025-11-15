// Step 6 – TraceServiceImpl
//“Your implementation of the gRPC service using the Java service.”
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
import java.util.UUID;

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

        // product_id comes as string (UUID text)
        String productIdText = req.getProductId();

        // Service handles conversion + DB call
        List<String> regs =
                recallService.getAnimalRegistrationNumbersByProductId(productIdText);

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

        // Service returns List<UUID> -> convert to List<String> for the proto response
        List<String> productIds = recallService
                .getProductIdsByAnimalRegistrationNumber(animalReg)
                .stream()
                .map(UUID::toString)
                .toList();

        GetProductsByAnimalResponse resp = GetProductsByAnimalResponse.newBuilder()
                .addAllProductIds(productIds)
                .build();

        out.onNext(resp);
        out.onCompleted();
    }
}
