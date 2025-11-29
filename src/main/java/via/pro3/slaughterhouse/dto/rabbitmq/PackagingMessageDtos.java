package via.pro3.slaughterhouse.dto.rabbitmq;

import via.pro3.slaughterhouse.entity.ProductKind;

import java.util.List;

/**
 * Message for creating products when DB is down.
 */
public class PackagingMessageDtos {

    private ProductKind kind;
    private List<Long> partIds;

    public PackagingMessageDtos() {
    }

    public PackagingMessageDtos(ProductKind kind, List<Long> partIds) {
        this.kind = kind;
        this.partIds = partIds;
    }

    // getters / setters

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
