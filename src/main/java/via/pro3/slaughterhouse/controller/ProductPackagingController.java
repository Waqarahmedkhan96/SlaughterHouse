package via.pro3.slaughterhouse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import via.pro3.slaughterhouse.dto.ProductPackagingDtos;
import via.pro3.slaughterhouse.service.ProductPackagingService;

@RestController
@RequestMapping("/products")
public class ProductPackagingController {

    private final ProductPackagingService productService;

    public ProductPackagingController(ProductPackagingService productService) {
        this.productService = productService;
    }

    // ---------- CREATE ----------
    // create new product
    @PostMapping
    public ResponseEntity<ProductPackagingDtos.ProductDto> createProduct(
            @RequestBody ProductPackagingDtos.CreateProductDto dto) {

        ProductPackagingDtos.ProductDto created = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ---------- READ ----------
    // get product by id
    @GetMapping("/{id}")
    public ResponseEntity<ProductPackagingDtos.ProductDto> getProduct(@PathVariable Long id) {
        ProductPackagingDtos.ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // get all products
    @GetMapping
    public ResponseEntity<ProductPackagingDtos.ProductListDto> getAllProducts() {
        ProductPackagingDtos.ProductListDto products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // ---------- UPDATE ----------
    // update existing product
    @PutMapping("/{id}")
    public ResponseEntity<ProductPackagingDtos.ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductPackagingDtos.UpdateProductDto dto) {

        ProductPackagingDtos.ProductDto updated = productService.updateProduct(id, dto);
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
