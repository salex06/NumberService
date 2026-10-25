package github.salex9206.user.auth.security;

import github.salex9206.user.auth.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtService {
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public JwtService(JwtConfig cfg) {
        this.jwtConfig = cfg;
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(cfg.getSecretKey()));
    }

    public String extractUsername(String bearerToken) {
        return extractClaimBody(bearerToken, Claims::getSubject);
    }

    public Date extractExpiry(String bearerToken) throws JwtException {
        return extractClaimBody(bearerToken, Claims::getExpiration);
    }

    public <T> T extractClaimBody(String bearerToken, Function<Claims, T> claimsResolver) throws JwtException {
        return claimsResolver.apply(extractClaims(bearerToken));
    }

    public String createToken(Map<String, Object> claims, String subject) {
        Date expiryDate = Date.from(Instant.ofEpochMilli(
                System.currentTimeMillis() + jwtConfig.getValidity().toMillis())
        );

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        if (isTokenExpired(token))
            return false;

        final String userName = extractUsername(token);
        return userName.equals(userDetails.getUsername());
    }

    private Claims extractClaims(String bearerToken) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(bearerToken)
                .getBody();
    }

    private boolean isTokenExpired(String bearerToken) {
        try {
            return extractExpiry(bearerToken).before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }
}
