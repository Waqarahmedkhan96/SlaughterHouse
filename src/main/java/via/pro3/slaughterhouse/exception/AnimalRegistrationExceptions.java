package via.pro3.slaughterhouse.exception;

/**
 * All Animal-related exceptions in one class.
 */
public class AnimalRegistrationExceptions {

    // not found
    public static class AnimalNotFoundException extends RuntimeException {
        public AnimalNotFoundException(String registrationNumber) {
            super("Animal not found: " + registrationNumber);
        }
    }

    // already exists
    public static class AnimalAlreadyExistsException extends RuntimeException {
        public AnimalAlreadyExistsException(String registrationNumber) {
            super("Animal already exists: " + registrationNumber);
        }
    }
}
