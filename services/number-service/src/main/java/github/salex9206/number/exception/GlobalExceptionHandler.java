package github.salex9206.number.exception;

import github.salex9206.shared.web.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidNumberException.class)
    public ResponseEntity<ErrorResponse> handleInvalidNumber(InvalidNumberException e) {
        ErrorResponse resp = new ErrorResponse(HttpStatus.UNPROCESSABLE_CONTENT.value(), e.getMessage());

        return ResponseEntity.unprocessableContent().body(resp);
    }
}
