package github.salex9206.number.exception;

public class NumberNotFoundException extends RuntimeException{
    public NumberNotFoundException(String message) {
        super(message);
    }

    public NumberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
