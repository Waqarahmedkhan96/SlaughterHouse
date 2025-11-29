// 3. Station 3, products are packed for distribution.
package via.pro3.slaughterhouse.service;

import org.springframework.dao.DataAccessException; // DB errors
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import via.pro3.slaughterhouse.dto.rest.PackagingDtos;
import via.pro3.slaughterhouse.entity.Part;
import via.pro3.slaughterhouse.entity.Product;
import via.pro3.slaughterhouse.entity.ProductKind;
import via.pro3.slaughterhouse.exception.ButcheringExceptions;
import via.pro3.slaughterhouse.exception.PackagingExceptions;
import via.pro3.slaughterhouse.rabbitmq.publisher.PackagingMessagePublisher;
import via.pro3.slaughterhouse.repository.PartRepository;
import via.pro3.slaughterhouse.repository.ProductRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PackagingService {

    private static final int MIN_UNIQUE_TYPES_FOR_HALF_ANIMAL = 3; // simple rule

    private final ProductRepository productRepository;
    private final PartRepository partRepository;
    private final PackagingMessagePublisher productMessagePublisher; // product publisher

    public PackagingService(ProductRepository productRepository,
                            PartRepository partRepository,
                            PackagingMessagePublisher productMessagePublisher) {
        this.productRepository = productRepository;
        this.partRepository = partRepository;
        this.productMessagePublisher = productMessagePublisher;
    }

    // ---------- CREATE ----------
    // create new product (no @Transactional – offline fallback)
    public PackagingDtos.ProductDto createProduct(PackagingDtos.CreateProductDto dto) {
        if (dto.getPartIds() == null || dto.getPartIds().isEmpty()) {
            throw new PackagingExceptions.InvalidProductCompositionException(
                    "Product must contain at least one part");
        }

        try {
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
        } catch (DataAccessException ex) {
            // DB offline
            productMessagePublisher.sendProductCreation(dto); // queue fallback

            PackagingDtos.ProductDto buffered = new PackagingDtos.ProductDto();
            buffered.setId(null); // id pending
            buffered.setKind(dto.getKind());
            buffered.setPartIds(dto.getPartIds());
            return buffered;
        }
    }

    // ---------- READ ----------
    // get product by id
    @Transactional(readOnly = true)
    public PackagingDtos.ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new PackagingExceptions.ProductNotFoundException(id));
        return toProductDto(product);
    }

    // get all products
    @Transactional(readOnly = true)
    public PackagingDtos.ProductListDto getAllProducts() {
        List<Product> products = productRepository.findAll();
        PackagingDtos.ProductListDto listDto = new PackagingDtos.ProductListDto();
        listDto.setProducts(products.stream().map(this::toProductDto).toList());
        return listDto;
    }

    // ---------- UPDATE ----------
    // update existing product
    @Transactional // write transaction
    public PackagingDtos.ProductDto updateProduct(
            Long id,
            PackagingDtos.UpdateProductDto dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new PackagingExceptions.ProductNotFoundException(id));

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
    @Transactional // write transaction
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new PackagingExceptions.ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    // ---------- HELPERS ----------
    // load parts by ids
    private Set<Part> loadParts(List<Long> partIds) {
        Set<Part> parts = new HashSet<>();
        for (Long partId : partIds) {
            Part part = partRepository.findById(partId)
                    .orElseThrow(() -> new ButcheringExceptions.PartNotFoundException(partId));
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
            throw new PackagingExceptions.InvalidProductCompositionException(
                    "SAME_TYPE product must contain parts of the same type");
        }
    }

    // check HALF_ANIMAL: at least N unique part types
    private void validateHalfAnimalParts(Set<Part> parts) {
        Set<String> uniqueTypes = parts.stream()
                .map(Part::getType)
                .collect(Collectors.toSet());

        if (uniqueTypes.size() < MIN_UNIQUE_TYPES_FOR_HALF_ANIMAL) {
            throw new PackagingExceptions.InvalidProductCompositionException(
                    "HALF_ANIMAL product must contain parts of at least "
                            + MIN_UNIQUE_TYPES_FOR_HALF_ANIMAL + " different types");
        }
    }

    // convert product entity to dto
    private PackagingDtos.ProductDto toProductDto(Product product) {
        PackagingDtos.ProductDto dto = new PackagingDtos.ProductDto();
        dto.setId(product.getId());
        dto.setKind(product.getKind());
        dto.setPartIds(product.getParts().stream()
                .map(Part::getId)
                .toList());
        return dto;
    }
}
