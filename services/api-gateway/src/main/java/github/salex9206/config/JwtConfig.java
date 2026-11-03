package github.salex9206.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class JwtConfig {
    private String secretKey;

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
