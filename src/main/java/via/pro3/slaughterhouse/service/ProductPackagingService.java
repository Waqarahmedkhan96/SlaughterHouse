// 3. Station 3, products are packed for distribution.
package via.pro3.slaughterhouse.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import via.pro3.slaughterhouse.dto.ProductPackagingDtos;
import via.pro3.slaughterhouse.entity.Part;
import via.pro3.slaughterhouse.entity.Product;
import via.pro3.slaughterhouse.entity.ProductKind;
import via.pro3.slaughterhouse.exception.CuttingAndTrayExceptions;
import via.pro3.slaughterhouse.exception.ProductPackagingExceptions;
import via.pro3.slaughterhouse.repository.PartRepository;
import via.pro3.slaughterhouse.repository.ProductRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductPackagingService {

    private static final int MIN_UNIQUE_TYPES_FOR_HALF_ANIMAL = 3; // simple rule

    private final ProductRepository productRepository;
    private final PartRepository partRepository;

    public ProductPackagingService(ProductRepository productRepository,
                                   PartRepository partRepository) {
        this.productRepository = productRepository;
        this.partRepository = partRepository;
    }

    // ---------- CREATE ----------
    // create new product
    public ProductPackagingDtos.ProductDto createProduct(ProductPackagingDtos.CreateProductDto dto) {
        if (dto.getPartIds() == null || dto.getPartIds().isEmpty()) {
            throw new ProductPackagingExceptions.InvalidProductCompositionException(
                    "Product must contain at least one part");
        }

        Set<Part> parts = loadParts(dto.getPartIds());

        // validate composition based on kind
        if (dto.getKind() == ProductKind.SAME_TYPE) {
            validateSameTypeParts(parts);
        } else if (dto.getKind() == ProductKind.HALF_ANIMAL) {
            validateHalfAnimalParts(parts);
        }

        Product product = new Product();
        product.setKind(dto.getKind());
        product.setParts(parts);

        Product saved = productRepository.save(product);
        return toProductDto(saved);
    }

    // ---------- READ ----------
    // get product by id
    public ProductPackagingDtos.ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductPackagingExceptions.ProductNotFoundException(id));
        return toProductDto(product);
    }

    // get all products
    public ProductPackagingDtos.ProductListDto getAllProducts() {
        List<Product> products = productRepository.findAll();
        ProductPackagingDtos.ProductListDto listDto = new ProductPackagingDtos.ProductListDto();
        listDto.setProducts(products.stream().map(this::toProductDto).toList());
        return listDto;
    }

    // ---------- UPDATE ----------
    // update existing product
    public ProductPackagingDtos.ProductDto updateProduct(
            Long id,
            ProductPackagingDtos.UpdateProductDto dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductPackagingExceptions.ProductNotFoundException(id));

        // determine new kind (if changed)
        ProductKind newKind = product.getKind();
        if (dto.getKind() != null) {
            newKind = dto.getKind();
        }

        // determine new parts (if changed)
        Set<Part> newParts = product.getParts();
        if (dto.getPartIds() != null) {
            newParts = loadParts(dto.getPartIds());
        }

        // validate according to (possibly updated) kind
        if (newKind == ProductKind.SAME_TYPE) {
            validateSameTypeParts(newParts);
        } else if (newKind == ProductKind.HALF_ANIMAL) {
            validateHalfAnimalParts(newParts);
        }

        // apply changes
        product.setKind(newKind);
        product.setParts(newParts);

        Product saved = productRepository.save(product);
        return toProductDto(saved);
    }

    // ---------- DELETE ----------
    // delete product by id
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductPackagingExceptions.ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    // ---------- HELPERS ----------
    // load parts by ids
    private Set<Part> loadParts(List<Long> partIds) {
        Set<Part> parts = new HashSet<>();
        for (Long partId : partIds) {
            Part part = partRepository.findById(partId)
                    .orElseThrow(() -> new CuttingAndTrayExceptions.PartNotFoundException(partId));
            parts.add(part);
        }
        return parts;
    }

    // check SAME_TYPE: all parts same type
    private void validateSameTypeParts(Set<Part> parts) {
        String type = parts.iterator().next().getType();
        boolean allSameType = parts.stream()
                .allMatch(p -> p.getType().equals(type));
        if (!allSameType) {
            throw new ProductPackagingExceptions.InvalidProductCompositionException(
                    "SAME_TYPE product must contain parts of the same type");
        }
    }

    // check HALF_ANIMAL: at least N unique part types
    private void validateHalfAnimalParts(Set<Part> parts) {
        Set<String> uniqueTypes = parts.stream()
                .map(Part::getType)
                .collect(Collectors.toSet());

        if (uniqueTypes.size() < MIN_UNIQUE_TYPES_FOR_HALF_ANIMAL) {
            throw new ProductPackagingExceptions.InvalidProductCompositionException(
                    "HALF_ANIMAL product must contain parts of at least "
                            + MIN_UNIQUE_TYPES_FOR_HALF_ANIMAL + " different types");
        }
    }

    // convert product entity to dto
    private ProductPackagingDtos.ProductDto toProductDto(Product product) {
        ProductPackagingDtos.ProductDto dto = new ProductPackagingDtos.ProductDto();
        dto.setId(product.getId());
        dto.setKind(product.getKind());
        dto.setPartIds(product.getParts().stream()
                .map(Part::getId)
                .toList());
        return dto;
    }
}
