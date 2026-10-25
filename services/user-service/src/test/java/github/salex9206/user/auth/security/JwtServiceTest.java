package github.salex9206.user.auth.security;

import github.salex9206.user.auth.config.JwtConfig;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Base64;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtConfig cfg = new JwtConfig();
        cfg.setSecretKey(Base64.getEncoder().encodeToString(
                "0123456789abcdef0123456789abcdef".getBytes())); // 32 байта
        cfg.setValidity(Duration.ofMinutes(15));
        jwtService = new JwtService(cfg);
    }

    @Test
    void extractUsername_invalidJwt_throwsException() {
        String bearerToken = "123";

        assertThrows(MalformedJwtException.class, () -> jwtService.extractUsername(bearerToken));
    }

    @Test
    void extractUsername_correctJwt_returnSubject() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ikl2YW4iLCJhZG1pbiI6dHJ1ZSwiaWF0IjozMjUyOTM2Nzg0M30.1Hv0WA8xWSBSTvZjsszwY_-1F_qd_vQb8i8mWD6Hwuo";
        String expectedName = "1234567890";

        String actualName = jwtService.extractUsername(token);

        assertEquals(expectedName, actualName);
    }

    @Test
    void validateToken_invalidUsername_returnFalse() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ikl2YW4iLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxMTMwMjIxODQzLCJleHAiOjExMzAyMjE4NDN9.Z-HeB4ky7EzGCD_D3vZ3vaUNhObw7cFqLjsv85DcFzE";
        UserDetails details = User.builder().username("InvalidName").password("123456").build();

        boolean isValid = jwtService.validateToken(token, details);

        assertFalse(isValid);
    }

    @Test
    void validateToken_tokenExpired_returnFalse() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ikl2YW4iLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxMTMwMjIxODQzLCJleHAiOjExMzAyMjE4NDN9.Z-HeB4ky7EzGCD_D3vZ3vaUNhObw7cFqLjsv85DcFzE";
        UserDetails details = User.builder().username("1234567890").password("123456").build();

        boolean isValid = jwtService.validateToken(token, details);

        assertFalse(isValid);
    }

    @Test
    void validateToken_tokenValid_returnTrue() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ikl2YW4iLCJhZG1pbiI6dHJ1ZSwiZXhwIjo0Mjg1ODk5MDQzfQ.gn6oD59vd8lSR4WnSrJaP1b0Hy_d3l_Ao7eEz-Xqg7s";
        UserDetails details = User.builder().username("1234567890").password("123456").build();

        boolean isValid = jwtService.validateToken(token, details);

        assertTrue(isValid);
    }

    @Test
    void createToken_returnsValidToken() {
        String subject = "Ivan";

        String token = jwtService.createToken(Collections.emptyMap(), subject);

        assertNotNull(token);
        assertThat(jwtService.extractUsername(token)).isEqualTo(subject);
    }
}
