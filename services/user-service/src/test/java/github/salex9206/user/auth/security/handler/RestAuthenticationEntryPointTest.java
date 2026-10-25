package github.salex9206.user.auth.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.salex9206.user.auth.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class RestAuthenticationEntryPointTest {
    private RestAuthenticationEntryPoint entryPoint;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        entryPoint = new RestAuthenticationEntryPoint();
    }

    @Test
    @DisplayName("exception в атрибуте: 401, JSON, message из исключения")
    void commence_writes401WithMessageFromRequestAttribute() throws Exception {
        // Arrange
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var cause = new BadCredentialsException("Bad credentials");
        request.setAttribute("exception", cause);

        // Act
        entryPoint.commence(request, response,
                new InsufficientAuthenticationException("no auth"));

        // Assert
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON_VALUE);
        ErrorResponse body = objectMapper.readValue(
                response.getContentAsByteArray(), ErrorResponse.class);
        assertThat(body.getCode()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(body.getMessage()).isEqualTo("Bad credentials");
    }

    @Test
    @DisplayName("exception отсутствует: 401 без NPE")
    void commence_WhenNoExceptionAttr_ThenWritesCorrect401() throws Exception {
        // Arrange
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        // Act
        entryPoint.commence(request, response,
                new InsufficientAuthenticationException("Full authentication is required"));

        // Assert
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON_VALUE);
        ErrorResponse body = objectMapper.readValue(
                response.getContentAsByteArray(), ErrorResponse.class);
        assertThat(body.getCode()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(body.getMessage()).isEqualTo("Authentication required");
    }

    @Test
    @DisplayName("Content-Type = application/json")
    void commence_setsCorrectJsonContentType() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new InsufficientAuthenticationException("x"));

        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON_VALUE);
    }

    @Test
    @DisplayName("Тело ответа — валидный непустой JSON (Объект ErrorResponse)")
    void commence_writesValidErrorResponse() throws Exception {
        // Arrange
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        // Act
        entryPoint.commence(request, response,
                new InsufficientAuthenticationException("x"));

        // Assert
        assertThat(response.getContentAsByteArray()).isNotEmpty();
        ErrorResponse body = objectMapper.readValue(
                response.getContentAsByteArray(), ErrorResponse.class);
        assertThat(body.getCode()).isEqualTo(401);
        assertThat(body.getMessage()).isNotBlank();
    }
}