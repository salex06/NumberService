package github.salex9206.user.auth.service;

import github.salex9206.shared.auth.token.properties.JwtProperties;
import github.salex9206.user.auth.api.dto.TokenRequest;
import github.salex9206.user.auth.api.dto.TokenResponse;
import github.salex9206.user.auth.domain.User;
import github.salex9206.user.auth.repository.UserRepository;
import github.salex9206.user.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private static final String USERNAME = "ivan_petrov";
    private static final String PASSWORD = "S3cret!pass";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ISSUED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.payload.signature";
    private static final Duration VALIDITY = Duration.ofMinutes(15);

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private UserRepository userRepository;

    private JwtProperties jwtProperties;
    @InjectMocks private TokenService tokenService;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecretKey("ab515eee845d20d56e937eae57dcc62307c7ce4ec0e5d8d5bf44481cd0ad032a");
        jwtProperties.setValidity(VALIDITY);

        // @InjectMocks не умеет инжектить JwtProperties (не @Mock) — создаём сервис вручную
        tokenService = new TokenService(authenticationManager, jwtService, userRepository, jwtProperties);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private User persistedUser() {
        var user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setPasswordHash("$2a$12$hashed");
        return user;
    }

    private TokenRequest request() {
        return new TokenRequest(USERNAME, PASSWORD);
    }

    private void stubHappyPath() {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findUserByUsername(USERNAME))
                .thenReturn(Optional.of(persistedUser()));
        when(jwtService.createToken(anyMap(), anyString()))
                .thenReturn(ISSUED_TOKEN);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> captureClaims() {
        var captor = ArgumentCaptor.forClass(Map.class);
        verify(jwtService).createToken(captor.capture(), anyString());
        return (Map<String, Object>) captor.getValue();
    }

    // ==================================================================
    // HAPPY PATH
    // ==================================================================

    @Nested
    @DisplayName("generateToken — happy path")
    class HappyPath {

        @Test
        @DisplayName("Возвращает accessToken из JwtService")
        void returnsAccessTokenFromJwtService() {
            stubHappyPath();

            TokenResponse response = tokenService.generateToken(request());

            assertThat(response.accessToken()).isEqualTo(ISSUED_TOKEN);
        }

        @Test
        @DisplayName("tokenType = \"Bearer\"")
        void tokenTypeIsBearer() {
            stubHappyPath();

            TokenResponse response = tokenService.generateToken(request());

            assertThat(response.tokenType()).isEqualTo("Bearer");
        }

        @Test
        @DisplayName("expiresIn = validity из JwtProperties (в секундах)")
        void expiresInComesFromJwtProperties() {
            stubHappyPath();

            TokenResponse response = tokenService.generateToken(request());

            assertThat(response.expiresIn()).isEqualTo(VALIDITY.getSeconds()); // 900
        }

        @Test
        @DisplayName("expiresIn отражает изменённую validity")
        void expiresInTracksConfiguredValidity() {
            jwtProperties.setValidity(Duration.ofHours(1));
            stubHappyPath();

            TokenResponse response = tokenService.generateToken(request());

            assertThat(response.expiresIn()).isEqualTo(3600);
        }
    }

    // ==================================================================
    // ВЗАИМОДЕЙСТВИЕ С AuthenticationManager
    // ==================================================================

    @Nested
    @DisplayName("generateToken — AuthenticationManager")
    class AuthenticationInteraction {

        @Test
        @DisplayName("Передаёт username и password в UsernamePasswordAuthenticationToken")
        void passesCredentialsToAuthenticationManager() {
            stubHappyPath();

            tokenService.generateToken(request());

            var captor = ArgumentCaptor.forClass(Authentication.class);
            verify(authenticationManager).authenticate(captor.capture());

            Authentication passed = captor.getValue();
            assertThat(passed).isInstanceOf(UsernamePasswordAuthenticationToken.class);
            assertThat(passed.getPrincipal()).isEqualTo(USERNAME);
            assertThat(passed.getCredentials()).isEqualTo(PASSWORD);
        }

        @Test
        @DisplayName("BadCredentialsException пробрасывается, JwtService не вызывается")
        void badCredentials_propagatesAndSkipsTokenGeneration() {
            when(authenticationManager.authenticate(any(Authentication.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> tokenService.generateToken(request()))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Bad credentials");

            verifyNoInteractions(jwtService, userRepository);
        }

        @Test
        @DisplayName("DisabledException пробрасывается, JwtService не вызывается")
        void disabledAccount_propagatesAndSkipsTokenGeneration() {
            when(authenticationManager.authenticate(any(Authentication.class)))
                    .thenThrow(new DisabledException("User is disabled"));

            assertThatThrownBy(() -> tokenService.generateToken(request()))
                    .isInstanceOf(DisabledException.class);

            verifyNoInteractions(jwtService, userRepository);
        }
    }

    // ==================================================================
    // ВЗАИМОДЕЙСТВИЕ С UserRepository
    // ==================================================================

    @Nested
    @DisplayName("generateToken — UserRepository")
    class RepositoryInteraction {

        @Test
        @DisplayName("Загружает пользователя по username из запроса")
        void loadsUserByUsername() {
            stubHappyPath();

            tokenService.generateToken(request());

            verify(userRepository).findUserByUsername(USERNAME);
        }

        @Test
        @DisplayName("Пользователь не найден после успешной аутентификации → UsernameNotFoundException")
        void userNotFoundAfterAuth_throwsUsernameNotFoundException() {
            when(authenticationManager.authenticate(any(Authentication.class)))
                    .thenReturn(mock(Authentication.class));
            when(userRepository.findUserByUsername(USERNAME))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> tokenService.generateToken(request()))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining(USERNAME);

            verifyNoInteractions(jwtService);
        }

        @Test
        @DisplayName("authenticate вызывается ДО findUserByUsername (порядок операций)")
        void authenticatesBeforeLoadingUser() {
            stubHappyPath();

            tokenService.generateToken(request());

            var inOrder = inOrder(authenticationManager, userRepository);
            inOrder.verify(authenticationManager).authenticate(any(Authentication.class));
            inOrder.verify(userRepository).findUserByUsername(USERNAME);
        }
    }

    // ==================================================================
    // CLAIMS
    // ==================================================================

    @Nested
    @DisplayName("generateToken — claims")
    class ClaimsTests {

        @Test
        @DisplayName("subject = user.id (UUID как строка)")
        void subjectIsUserId() {
            stubHappyPath();

            tokenService.generateToken(request());

            var captor = ArgumentCaptor.forClass(String.class);
            verify(jwtService).createToken(anyMap(), captor.capture());

            assertThat(captor.getValue()).isEqualTo(USER_ID.toString());
        }

        @Test
        @DisplayName("claims содержат username")
        void claimsContainUsername() {
            stubHappyPath();

            tokenService.generateToken(request());

            assertThat(captureClaims())
                    .containsEntry("username", USERNAME);
        }

        @Test
        @DisplayName("claims содержат roles (сейчас — пустой список; TODO)")
        void claimsContainRolesAsEmptyListForNow() {
            stubHappyPath();

            tokenService.generateToken(request());

            assertThat(captureClaims().get("roles"))
                    .isInstanceOf(List.class)
                    .asList()
                    .isEmpty();
        }

        @Test
        @DisplayName("claims — ровно два ключа: username и roles")
        void claimsHaveExpectedKeys() {
            stubHappyPath();

            tokenService.generateToken(request());

            assertThat(captureClaims())
                    .containsOnlyKeys("username", "roles");
        }
    }

    // ==================================================================
    // ВЗАИМОДЕЙСТВИЕ С JwtService
    // ==================================================================

    @Nested
    @DisplayName("generateToken — JwtService")
    class JwtServiceInteraction {

        @Test
        @DisplayName("JwtService вызывается ровно один раз")
        void callsJwtServiceOnce() {
            stubHappyPath();

            tokenService.generateToken(request());

            verify(jwtService, times(1)).createToken(anyMap(), anyString());
        }

        @Test
        @DisplayName("Исключение от JwtService пробрасывается наружу")
        void jwtServiceThrows_propagatesException() {
            when(authenticationManager.authenticate(any(Authentication.class)))
                    .thenReturn(mock(Authentication.class));
            when(userRepository.findUserByUsername(USERNAME))
                    .thenReturn(Optional.of(persistedUser()));
            when(jwtService.createToken(anyMap(), anyString()))
                    .thenThrow(new IllegalStateException("signing failed"));

            assertThatThrownBy(() -> tokenService.generateToken(request()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("signing failed");
        }
    }
}