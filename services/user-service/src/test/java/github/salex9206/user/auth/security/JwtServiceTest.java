package github.salex9206.user.auth.security;

import github.salex9206.shared.auth.token.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    // 64 hex-символа = 32 байта (256 бит) — минимум для HS256
    private static final String SECRET_HEX =
            "ab515eee845d20d56e937eae57dcc62307c7ce4ec0e5d8d5bf44481cd0ad032a";
    private static final String SUBJECT_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final Duration DEFAULT_VALIDITY = Duration.ofMinutes(15);

    private JwtProperties props;
    private JwtService jwtService;
    private SecretKey verificationKey;

    @BeforeEach
    void setUp() {
        props = new JwtProperties();
        props.setSecretKey(SECRET_HEX);
        props.setValidity(DEFAULT_VALIDITY);

        jwtService = new JwtService(props);
        verificationKey = Keys.hmacShaKeyFor(HexFormat.of().parseHex(SECRET_HEX));
    }

    /** Парсит токен тем же ключом — проверяет и подпись, и claims. */
    private Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(verificationKey)
                .build()
                .parseSignedClaims(token);
    }

    private Claims claimsOf(String token) {
        return parse(token).getPayload();
    }

    // ==================================================================
    // Конструктор
    // ==================================================================

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("Валидный hex-ключ 32 байта → сервис создаётся")
        void validKey_createsService() {
            assertThat(jwtService).isNotNull();
        }

        @Test
        @DisplayName("Слишком короткий ключ (< 32 байт) → WeakKeyException")
        void tooShortKey_throwsWeakKeyException() {
            var bad = new JwtProperties();
            bad.setSecretKey("abcdef0123456789");  // 8 байт — мало для HS256
            bad.setValidity(DEFAULT_VALIDITY);

            assertThatThrownBy(() -> new JwtService(bad))
                    .isInstanceOf(WeakKeyException.class);
        }

        @Test
        @DisplayName("Не-hex строка → IllegalArgumentException")
        void invalidHex_throwsIllegalArgumentException() {
            var bad = new JwtProperties();
            bad.setSecretKey("not-a-valid-hex-string");
            bad.setValidity(DEFAULT_VALIDITY);

            assertThatThrownBy(() -> new JwtService(bad))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ==================================================================
    // createToken — структура токена
    // ==================================================================

    @Nested
    @DisplayName("createToken — структура")
    class Structure {

        @Test
        @DisplayName("Возвращает непустой токен")
        void returnsNonBlankToken() {
            String token = jwtService.createToken(Map.of(), SUBJECT_ID);

            assertThat(token).isNotBlank();
        }

        @Test
        @DisplayName("Токен состоит из 3 частей (header.payload.signature)")
        void hasThreeParts() {
            String token = jwtService.createToken(Map.of(), SUBJECT_ID);

            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("Header содержит alg=HS256")
        void headerAlgorithmIsHS256() {
            String token = jwtService.createToken(Map.of(), SUBJECT_ID);

            String headerJson = new String(
                    Base64.getUrlDecoder().decode(token.split("\\.")[0]));
            assertThat(headerJson).contains("\"alg\":\"HS256\"");
        }

        @Test
        @DisplayName("Типичный токен < 1 KB (защита от раздувания claims)")
        void typicalToken_isUnderOneKb() {
            String token = jwtService.createToken(
                    Map.of("username", "ivan_petrov",
                            "roles", List.of("USER", "ADMIN")),
                    SUBJECT_ID);

            assertThat(token.length()).isLessThan(1024);
        }
    }

    // ==================================================================
    // createToken — claims
    // ==================================================================

    @Nested
    @DisplayName("createToken — claims")
    class ClaimsTests {

        @Test
        @DisplayName("Subject устанавливается из subjectId")
        void setsSubject() {
            String token = jwtService.createToken(Map.of(), SUBJECT_ID);

            assertThat(claimsOf(token).getSubject()).isEqualTo(SUBJECT_ID);
        }

        @Test
        @DisplayName("String claims сохраняются")
        void preservesStringClaims() {
            String token = jwtService.createToken(
                    Map.of("username", "ivan"), SUBJECT_ID);

            assertThat(claimsOf(token).get("username", String.class))
                    .isEqualTo("ivan");
        }

        @Test
        @DisplayName("List claims сохраняются")
        void preservesListClaims() {
            String token = jwtService.createToken(
                    Map.of("roles", List.of("USER", "ADMIN")), SUBJECT_ID);

            assertThat(claimsOf(token).get("roles", List.class))
                    .containsExactly("USER", "ADMIN");
        }

        @Test
        @DisplayName("Числовые и boolean claims сохраняются")
        void preservesNumericAndBooleanClaims() {
            String token = jwtService.createToken(
                    Map.of("counter", 42, "active", true), SUBJECT_ID);

            Claims claims = claimsOf(token);
            assertThat(((Number) claims.get("counter")).intValue()).isEqualTo(42);
            assertThat(claims.get("active", Boolean.class)).isTrue();
        }

        @Test
        @DisplayName("Пустая Map claims — токен валиден")
        void emptyClaims_works() {
            String token = jwtService.createToken(Map.of(), SUBJECT_ID);

            Claims claims = claimsOf(token);
            assertThat(claims.getSubject()).isEqualTo(SUBJECT_ID);
            // стандартные claim'ы присутствуют
            assertThat(claims.getIssuedAt()).isNotNull();
            assertThat(claims.getExpiration()).isNotNull();
        }

        @Test
        @DisplayName("Спецсимволы и Unicode в claims не ломают токен")
        void specialCharactersInClaims_works() {
            String token = jwtService.createToken(
                    Map.of("name", "Иван Петров", "note", "a;b:c\"d"), SUBJECT_ID);

            Claims claims = claimsOf(token);
            assertThat(claims.get("name", String.class)).isEqualTo("Иван Петров");
            assertThat(claims.get("note", String.class)).isEqualTo("a;b:c\"d");
        }
    }

    // ==================================================================
    // createToken — временные метки
    // ==================================================================

    @Nested
    @DisplayName("createToken — timestamps")
    class Timestamps {

        @Test
        @DisplayName("exp = now + validity (с допуском ±2 сек)")
        void expirationMatchesValidity() {
            Instant before = Instant.now();
            String token = jwtService.createToken(Map.of(), SUBJECT_ID);
            Instant after = Instant.now();

            Instant exp = claimsOf(token).getExpiration().toInstant();
            Instant expectedMin = before.plus(DEFAULT_VALIDITY).minusSeconds(2);
            Instant expectedMax = after.plus(DEFAULT_VALIDITY).plusSeconds(2);

            assertThat(exp).isBetween(expectedMin, expectedMax);
        }

        @Test
        @DisplayName("iat ≈ now (с допуском ±2 сек)")
        void issuedAtIsApproximatelyNow() {
            Instant before = Instant.now().minusSeconds(2);
            String token = jwtService.createToken(Map.of(), SUBJECT_ID);
            Instant after = Instant.now().plusSeconds(2);

            Instant iat = claimsOf(token).getIssuedAt().toInstant();

            assertThat(iat).isBetween(before, after);
        }

        @Test
        @DisplayName("exp > iat (токен не истёк в момент выпуска)")
        void expirationIsAfterIssuedAt() {
            String token = jwtService.createToken(Map.of(), SUBJECT_ID);

            Claims claims = claimsOf(token);
            assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
        }

        @Test
        @DisplayName("Разная validity → разный интервал между iat и exp")
        void differentValidity_producesDifferentExpiration() {
            var shortProps = new JwtProperties();
            shortProps.setSecretKey(SECRET_HEX);
            shortProps.setValidity(Duration.ofMinutes(1));
            var shortService = new JwtService(shortProps);

            var longProps = new JwtProperties();
            longProps.setSecretKey(SECRET_HEX);
            longProps.setValidity(Duration.ofHours(1));
            var longService = new JwtService(longProps);

            Date expShort = claimsOf(shortService.createToken(Map.of(), SUBJECT_ID)).getExpiration();
            Date expLong  = claimsOf(longService.createToken(Map.of(), SUBJECT_ID)).getExpiration();

            assertThat(expLong).isAfter(expShort);
        }
    }

    // ==================================================================
    // createToken — подпись
    // ==================================================================

    @Nested
    @DisplayName("createToken — signature")
    class SignatureTests {

        @Test
        @DisplayName("Токен верифицируется тем же ключом")
        void tokenVerifiableWithSameKey() {
            String token = jwtService.createToken(Map.of(), SUBJECT_ID);

            // parse() использует verificationKey — не бросает, значит подпись валидна
            assertThat(claimsOf(token).getSubject()).isEqualTo(SUBJECT_ID);
        }

        @Test
        @DisplayName("Токен НЕ верифицируется другим ключом → SignatureException")
        void tokenNotVerifiableWithDifferentKey() {
            String token = jwtService.createToken(Map.of(), SUBJECT_ID);

            SecretKey otherKey = Keys.hmacShaKeyFor(
                    HexFormat.of().parseHex(
                            "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"));

            assertThatThrownBy(() -> Jwts.parser()
                    .verifyWith(otherKey)
                    .build()
                    .parseSignedClaims(token))
                    .isInstanceOf(SignatureException.class);
        }

        @Test
        @DisplayName("Повреждённый токен → SignatureException")
        void tamperedToken_throwsSignatureException() {
            String token = jwtService.createToken(Map.of(), SUBJECT_ID);
            String tampered = token.substring(0, token.length() - 1)
                    + (token.endsWith("A") ? "B" : "A");

            assertThatThrownBy(() -> parse(tampered))
                    .isInstanceOf(SignatureException.class);
        }
    }
}