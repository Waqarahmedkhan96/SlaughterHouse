package via.pro3.slaughterhouse.exception;

/**
 * All station 2 specific exceptions.
 */
public class ButcheringExceptions {

    public static class TrayNotFoundException extends RuntimeException {
        public TrayNotFoundException(Long id) {
            super("Tray not found: " + id);
        }
    }

    public static class PartNotFoundException extends RuntimeException {
        public PartNotFoundException(Long id) {
            super("Part not found: " + id);
        }
    }

    public static class InvalidTrayForPartException extends RuntimeException {
        public InvalidTrayForPartException(String message) {
            super(message);
        }
    }

    public static class TrayCapacityExceededException extends RuntimeException {
        public TrayCapacityExceededException(Long trayId) {
            super("Tray capacity exceeded for tray: " + trayId);
        }
    }
}
