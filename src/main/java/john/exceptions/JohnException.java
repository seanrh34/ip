package john.exceptions;

/**
 * Represents an exception specific to John ChatBot operations.
 * This exception is thrown when the application encounters errors
 * during command parsing, task operations, or other business logic.
 */
public class JohnException extends Exception {

    /**
     * Constructs a new JohnException with the specified detail message.
     *
     * @param message The detail message explaining the exception.
     */
    public JohnException(String message) {
        super(message);
    }

}
