package vanadium.models.exceptions;

public class InvalidColorMappingException extends RuntimeException {

    public InvalidColorMappingException() {
        super();
    }

    public InvalidColorMappingException(String message) {
        super(message);
    }

    public InvalidColorMappingException(Throwable cause) {
        super(cause);
    }

    public InvalidColorMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}