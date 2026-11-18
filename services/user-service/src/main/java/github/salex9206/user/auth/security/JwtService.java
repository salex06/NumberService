package github.salex9206.user.auth.security;

import github.salex9206.shared.auth.token.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.Map;

@Component
@Slf4j
public class JwtService {
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties cfg) {
        this.jwtProperties = cfg;
        this.secretKey = Keys.hmacShaKeyFor(HexFormat.of().parseHex(cfg.getSecretKey()));
    }

    public String createToken(Map<String, Object> claims, String subjectId) {
        Date expiryDate = Date.from(Instant.ofEpochMilli(
                System.currentTimeMillis() + jwtProperties.getValidity().toMillis())
        );

        return Jwts.builder()
                .claims(claims)
                .subject(subjectId)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }
};
