package github.salex9206.number.exception;

import github.salex9206.shared.web.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidNumberException.class)
    public ResponseEntity<?> handleInvalidNumber(InvalidNumberException e) {
        return handle(e.getMessage(), HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @ExceptionHandler(NumberNotFoundException.class)
    public ResponseEntity<?> handleNumberNotFound(NumberNotFoundException e) {
        return handle(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> handle(String message, HttpStatus code) {
        ErrorResponse resp = new ErrorResponse(code.value(), message);

        return new ResponseEntity<>(resp, code);
    }
}
