package github.salex9206.user.auth.exception;

import github.salex9206.user.auth.api.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
    void handleMethodArgumentNotValidException_ReturnsCorrectResponse() {
        String errorMsg = "method argument is not valid";
        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
        when(ex.getMessage()).thenReturn(errorMsg);
        ErrorResponse resp = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMsg);

        ResponseEntity<?> entity = handler.handleMethodArgumentNotValidException(ex);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(entity.getBody()).isEqualTo(resp);
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