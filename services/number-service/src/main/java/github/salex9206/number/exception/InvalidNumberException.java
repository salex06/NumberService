package github.salex9206.number.exception;

import lombok.Getter;

@Getter
public class InvalidNumberException extends RuntimeException {
    private final String rawInput;

    public InvalidNumberException(String rawInput, String message) {
        super(message);
        this.rawInput = rawInput;
    }

    public InvalidNumberException(String rawInput, String message, Throwable cause) {
        super(message, cause);
        this.rawInput = rawInput;
    }

}
