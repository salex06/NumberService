package github.salex9206.user.auth.exception;

import github.salex9206.user.auth.api.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException exception) {
        return handle(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return handle(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<?> handleUserNotFoundException(UsernameNotFoundException exception) {
        return handle(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    private ResponseEntity<?> handle(HttpStatus code, String message) {
        ErrorResponse response = ErrorResponse.builder()
                .code(code.value())
                .message(message)
                .build();

        return new ResponseEntity<>(response, code);
    }
}
