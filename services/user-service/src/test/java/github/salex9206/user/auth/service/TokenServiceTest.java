package github.salex9206.user.auth.service;

import github.salex9206.user.auth.api.dto.TokenRequest;
import github.salex9206.user.auth.api.dto.TokenResponse;
import github.salex9206.user.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private static final String USERNAME = "ivan";
    private static final String PASSWORD = "S3cret!pass";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.payload.signature";

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AuthUserDetailsService userDetailsService;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TokenService tokenService;

    private Authentication successfulAuth;

    @BeforeEach
    void setUp() {
        UserDetails userDetails = User.builder()
                .username(USERNAME)
                .password("ignored")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        successfulAuth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    @Test
    @DisplayName("generateToken: успешная аутентификация - TokenResponse с токеном")
    void generateToken_WhenAuthenticated_ThenReturnToken() {
        // Arrange
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(successfulAuth);
        when(jwtService.createToken(anyMap(), eq(USERNAME))).thenReturn(TOKEN);

        // Act
        TokenResponse response = tokenService.generateToken(
                new TokenRequest(USERNAME, PASSWORD));

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(TOKEN);
    }

    @Test
    @DisplayName("generateToken: в AuthenticationManager передаются username и password")
    void generateToken_passesCredentialsToAuthenticationManager() {
        // Arrange
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(successfulAuth);
        when(jwtService.createToken(anyMap(), anyString())).thenReturn(TOKEN);

        // Act
        tokenService.generateToken(new TokenRequest(USERNAME, PASSWORD));

        // Assert
        var captor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(captor.capture());
        Authentication passed = captor.getValue();
        assertThat(Objects.requireNonNull(passed.getPrincipal())).isEqualTo(USERNAME);
        assertThat(Objects.requireNonNull(passed.getCredentials())).isEqualTo(PASSWORD);
        assertThat(passed).isInstanceOf(UsernamePasswordAuthenticationToken.class);
    }

    @Test
    @DisplayName("generateToken: subject JWT = username из Authentication")
    void generateToken_usesAuthenticatedUsernameAsSubject() {
        // Arrange
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(successfulAuth);
        when(jwtService.createToken(anyMap(), anyString())).thenReturn(TOKEN);

        // Act
        tokenService.generateToken(new TokenRequest(USERNAME, PASSWORD));

        // Assert
        verify(jwtService).createToken(anyMap(), eq(USERNAME));
    }

    @Test
    @DisplayName("generateToken: не делает повторный loadUserByUsername")
    void generateToken_doesNotReloadUserDetails() {
        // Arrange
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(successfulAuth);
        when(jwtService.createToken(anyMap(), anyString())).thenReturn(TOKEN);

        // Act
        tokenService.generateToken(new TokenRequest(USERNAME, PASSWORD));

        // Assert
        verifyNoInteractions(userDetailsService);
    }

    @Test
    @DisplayName("generateToken: BadCredentialsException пробрасывается как есть")
    void generateToken_WhenBadCredentials_ThenPropagateException() {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> tokenService.generateToken(
                new TokenRequest(USERNAME, "wrong")))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");

        verifyNoInteractions(jwtService);
    }


    @Test
    @DisplayName("generateToken: исключение от JwtService пробрасывается")
    void generateToken_jwtFailure_propagatesException() {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(successfulAuth);
        when(jwtService.createToken(anyMap(), anyString()))
                .thenThrow(new IllegalStateException("key too weak"));

        assertThatThrownBy(() -> tokenService.generateToken(
                new TokenRequest(USERNAME, PASSWORD)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("key too weak");
    }
}