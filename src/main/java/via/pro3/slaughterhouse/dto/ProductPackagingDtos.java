package via.pro3.slaughterhouse.dto;

import via.pro3.slaughterhouse.entity.ProductKind;

import java.util.ArrayList;
import java.util.List;

/**
 * All DTOs for Station 3 (product packaging).
 */
public class ProductPackagingDtos {

    // ---------- CREATE ----------
    // create product dto
    public static class CreateProductDto {
        private ProductKind kind;        // SAME_TYPE or HALF_ANIMAL
        private List<Long> partIds = new ArrayList<>();

        public CreateProductDto() {}

        public ProductKind getKind() {
            return kind;
        }

        public void setKind(ProductKind kind) {
            this.kind = kind;
        }

        public List<Long> getPartIds() {
            return partIds;
        }

        public void setPartIds(List<Long> partIds) {
            this.partIds = partIds;
        }
    }

    // ---------- READ ONE ----------
    // product details dto
    public static class ProductDto {
        private Long id;
        private ProductKind kind;
        private List<Long> partIds = new ArrayList<>();

        public ProductDto() {}

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public ProductKind getKind() {
            return kind;
        }

        public void setKind(ProductKind kind) {
            this.kind = kind;
        }

        public List<Long> getPartIds() {
            return partIds;
        }

        public void setPartIds(List<Long> partIds) {
            this.partIds = partIds;
        }
    }

    // ---------- UPDATE ----------
    // update product dto
    public static class UpdateProductDto {
        // nullable fields: only send what you want to change
        private ProductKind kind;        // optional
        private List<Long> partIds;      // optional

        public UpdateProductDto() {}

        public ProductKind getKind() {
            return kind;
        }

        public void setKind(ProductKind kind) {
            this.kind = kind;
        }

        public List<Long> getPartIds() {
            return partIds;
        }

        public void setPartIds(List<Long> partIds) {
            this.partIds = partIds;
        }
    }

    // ---------- READ MANY ----------
    // products list dto
    public static class ProductListDto {
        private List<ProductDto> products = new ArrayList<>();

        public ProductListDto() {}

        public List<ProductDto> getProducts() {
            return products;
        }

        public void setProducts(List<ProductDto> products) {
            this.products = products;
        }
    }
}
