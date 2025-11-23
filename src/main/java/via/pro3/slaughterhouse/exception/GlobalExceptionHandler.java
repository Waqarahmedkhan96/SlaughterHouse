package via.pro3.slaughterhouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handles all exceptions globally for REST controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // handle AnimalNotFoundException
    @ExceptionHandler(AnimalExceptions.AnimalNotFoundException.class)
    public ResponseEntity<String> handleAnimalNotFound(AnimalExceptions.AnimalNotFoundException ex) {
        // 404 Not Found
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    // handle AnimalAlreadyExistsException
    @ExceptionHandler(AnimalExceptions.AnimalAlreadyExistsException.class)
    public ResponseEntity<String> handleAnimalAlreadyExists(AnimalExceptions.AnimalAlreadyExistsException ex) {
        // 409 Conflict
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    // optional: generic invalid input
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        // 400 Bad Request
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
