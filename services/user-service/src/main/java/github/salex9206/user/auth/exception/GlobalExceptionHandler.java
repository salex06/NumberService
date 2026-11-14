package github.salex9206.user.auth.exception;

import github.salex9206.shared.web.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException exception) {
        return handle(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            message = "Validation failed";
        }

        return handle(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<?> handleUserNotFoundException(UsernameNotFoundException exception) {
        return handle(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistsException.class})
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return handle(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UserSavingException.class)
    public ResponseEntity<?> handleSaving(UserSavingException ex) {
        return handle(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<?> handle(HttpStatus code, String message) {
        ErrorResponse response = ErrorResponse.builder()
                .code(code.value())
                .message(message)
                .build();

        return new ResponseEntity<>(response, code);
    }
}
