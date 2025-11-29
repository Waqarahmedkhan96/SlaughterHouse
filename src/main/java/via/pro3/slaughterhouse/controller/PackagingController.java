package via.pro3.slaughterhouse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import via.pro3.slaughterhouse.dto.rest.PackagingDtos;
import via.pro3.slaughterhouse.service.PackagingService;

import java.util.Map;

@RestController
@RequestMapping("/products")
public class PackagingController {

    private final PackagingService productService;

    public PackagingController(PackagingService productService) {
        this.productService = productService;
    }

    // ---------- CREATE ----------
    // create new product
    @PostMapping
    public ResponseEntity<?> createProduct( // generic body
                                            @RequestBody PackagingDtos.CreateProductDto dto) {

        PackagingDtos.ProductDto created = productService.createProduct(dto);

        if (created == null) {
            // queued (db down)
            Map<String, Object> body = Map.of(
                    "status", "QUEUED",                               // queued flag
                    "message", "Database unavailable. Product stored in queue.",
                    "kind", dto.getKind() != null ? dto.getKind().name() : null,
                    "partIds", dto.getPartIds()                       // requested parts
            );
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED) // 202
                    .body(body);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED) // 201
                .body(created);
    }

    // ---------- READ ----------
    // get product by id
    @GetMapping("/{id}")
    public ResponseEntity<PackagingDtos.ProductDto> getProduct(@PathVariable Long id) {
        PackagingDtos.ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // get all products
    @GetMapping
    public ResponseEntity<PackagingDtos.ProductListDto> getAllProducts() {
        PackagingDtos.ProductListDto products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // ---------- UPDATE ----------
    // update existing product
    @PutMapping("/{id}")
    public ResponseEntity<PackagingDtos.ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody PackagingDtos.UpdateProductDto dto) {

        PackagingDtos.ProductDto updated = productService.updateProduct(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ---------- DELETE ----------
    // delete product by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
