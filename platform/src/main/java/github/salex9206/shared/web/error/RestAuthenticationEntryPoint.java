package github.salex9206.shared.web.error;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException, ServletException {
        Object attr = request.getAttribute("exception");
        String message = (attr instanceof Exception ex && ex.getMessage() != null)
                ? ex.getMessage()
                : "Authentication required";

        if (attr instanceof Exception)
            log.warn("Authentication failed: {}", message);
        else
            log.debug("Authentication required: {}", authException.getMessage());

        ErrorResponse body = ErrorResponse.builder()
                .code(HttpServletResponse.SC_UNAUTHORIZED)
                .message(message)
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
