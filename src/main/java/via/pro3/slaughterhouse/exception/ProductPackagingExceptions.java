package via.pro3.slaughterhouse.exception;

/**
 * All station 3 specific exceptions.
 */
public class ProductPackagingExceptions {

    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(Long id) {
            super("Product not found: " + id);
        }
    }

    public static class InvalidProductCompositionException extends RuntimeException {
        public InvalidProductCompositionException(String message) {
            super(message);
        }
    }
}
