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

        // product_id comes as string (text form of Long)
        String productIdText = req.getProductId();

        // 🔹 convert String → Long for the service layer
        Long productId = Long.parseLong(productIdText);

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

        // Service returns List<Long> -> convert to List<String> for proto response
        List<String> productIds = recallService
                .getProductIdsByAnimalRegistrationNumber(animalReg)
                .stream()
                .map(id -> id.toString())       // 🔹 avoid ambiguous Long::toString
                .toList();

        GetProductsByAnimalResponse resp = GetProductsByAnimalResponse.newBuilder()
                .addAllProductIds(productIds)
                .build();

        out.onNext(resp);
        out.onCompleted();
    }
}
