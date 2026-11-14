package github.salex9206.user.auth.exception;

import github.salex9206.shared.web.error.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBadCredentialsException_ReturnsCorrectResponse() {
        String errorMsg = "bad credentials";
        BadCredentialsException ex = new BadCredentialsException(errorMsg);
        ErrorResponse resp = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), errorMsg);

        ResponseEntity<?> entity = handler.handleBadCredentialsException(ex);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(entity.getBody()).isEqualTo(resp);
    }

    @Test
    void handleMethodArgumentNotValidException_assemblesMessageFromFieldErrors() {
        // arrange
        var usernameError = new FieldError("registrationRequest", "username", "must not be blank");
        var passwordError = new FieldError("registrationRequest", "password", "size must be between 8 and 128");

        var bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(usernameError, passwordError));

        var ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        var expected = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "username: must not be blank; password: size must be between 8 and 128"
        );

        // act
        ResponseEntity<?> entity = handler.handleMethodArgumentNotValidException(ex);

        // assert
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(entity.getBody()).isEqualTo(expected);
        verify(ex, never()).getMessage();
    }

    @Test
    void handleUsernameNotFoundException_ReturnsCorrectResponse() {
        String errorMsg = "username not found";
        UsernameNotFoundException ex = new UsernameNotFoundException(errorMsg);
        ErrorResponse resp = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), errorMsg);

        ResponseEntity<?> entity = handler.handleUserNotFoundException(ex);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(entity.getBody()).isEqualTo(resp);
    }
}