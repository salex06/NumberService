package github.salex9206.user.auth.security;

import github.salex9206.user.auth.service.AuthUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.payload.signature";
    private static final String USERNAME = "ivan";

    @Mock
    private AuthUserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        userDetails = User.builder()
                .username(USERNAME)
                .password("ignored")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Без Authorization: цепочка продолжается, контекст пуст")
    void doFilterInternal_WhenNoAuthorizationHeader_ThenPassesThroughWithoutAuthentication() throws Exception {
        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Basic-заголовок игнорируется")
    void doFilterInternal_WhenNonBearerHeader_ThenPassesThrough() throws Exception {
        request.addHeader(AUTHORIZATION, "Basic dXNlcjpwYXNz");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Валидный токен: Authentication установлен с authorities")
    void doFilterInternal_WhenValidToken_ThenSetsAuthenticationInContext() throws Exception {
        // Arrange
        request.addHeader(AUTHORIZATION, "Bearer " + TOKEN);
        when(jwtService.extractUsername(TOKEN)).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        when(jwtService.validateToken(TOKEN, userDetails)).thenReturn(true);

        // Act
        filter.doFilter(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo(USERNAME);
        assertThat(auth.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
        assertThat(auth).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(auth.getDetails()).isNotNull();
    }

    @Test
    @DisplayName("validateToken=false: Authentication НЕ устанавливается")
    void doFilterInternal_WhenTokenWithFailedValidation_ThenDoesNotAuthenticate() throws Exception {
        // Arrange
        request.addHeader(AUTHORIZATION, "Bearer " + TOKEN);
        when(jwtService.extractUsername(TOKEN)).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        when(jwtService.validateToken(TOKEN, userDetails)).thenReturn(false);

        // Act
        filter.doFilter(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Уже установленный Authentication не перезаписывается")
    void doFilterInternal_WhenAlreadyAuthenticated_ThenDoesNotOverrideContext() throws Exception {
        // Arrange
        var existing = new UsernamePasswordAuthenticationToken(
                "existing", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(existing);
        request.addHeader(AUTHORIZATION, "Bearer " + TOKEN);
        when(jwtService.extractUsername(TOKEN)).thenReturn(USERNAME);

        // Act
        filter.doFilter(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        verify(jwtService, never()).validateToken(anyString(), any());
        verifyNoInteractions(userDetailsService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(existing);
    }

    @Test
    @DisplayName("Истёкший токен: exception в атрибуте, контекст пуст")
    void doFilterInternal_WhenExpiredToken_ThenDoNotAuthenticate() throws Exception {
        // Arrange
        request.addHeader(AUTHORIZATION, "Bearer " + TOKEN);
        var expired = new ExpiredJwtException(null, null, "expired");
        when(jwtService.extractUsername(TOKEN)).thenThrow(expired);

        // Act
        filter.doFilter(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        assertThat(request.getAttribute("exception")).isSameAs(expired);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Неверная подпись: SignatureException кладётся в атрибут, контекст пуст")
    void doFilterInternal_WhenTamperedSignature_ThenSetExceptionAttribute() throws Exception {
        // Arrange
        request.addHeader(AUTHORIZATION, "Bearer " + TOKEN);
        var signatureException = new SignatureException("JWT signature does not match");
        when(jwtService.extractUsername(TOKEN)).thenThrow(signatureException);

        // Act
        filter.doFilter(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        assertThat(request.getAttribute("exception")).isSameAs(signatureException);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Битый токен: MalformedJwtException кладётся в атрибут")
    void doFilterInternal_WhenMalformedToken_ThenSetExceptionAttribute() throws Exception {
        // Arrange
        request.addHeader(AUTHORIZATION, "Bearer " + TOKEN);
        var malformed = new MalformedJwtException("malformed");
        when(jwtService.extractUsername(TOKEN)).thenThrow(malformed);

        // Act
        filter.doFilter(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        assertThat(request.getAttribute("exception")).isSameAs(malformed);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Пустой токен: IllegalArgumentException кладётся в атрибут")
    void doFilterInternal_WhenEmptyToken_ThenSetsExceptionAttribute() throws Exception {
        // Arrange
        request.addHeader(AUTHORIZATION, "Bearer ");
        when(jwtService.extractUsername("")).thenThrow(new IllegalArgumentException("empty"));

        // Act
        filter.doFilter(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        assertThat(request.getAttribute("exception")).isInstanceOf(IllegalArgumentException.class);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
