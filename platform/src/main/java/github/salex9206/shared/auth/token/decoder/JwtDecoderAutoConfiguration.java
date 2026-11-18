package github.salex9206.shared.auth.token.decoder;

import github.salex9206.shared.auth.token.properties.JwtProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.HexFormat;

@AutoConfiguration
@ConditionalOnClass({JwtDecoder.class, SecurityFilterChain.class})
@ConditionalOnProperty(prefix = "app.jwt", name = "secret-key")
@EnableConfigurationProperties(JwtProperties.class)
public class JwtDecoderAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    public JwtDecoder jwtDecoder(JwtProperties props) {
        byte[] secret = HexFormat.of().parseHex(props.getSecretKey());
        if (secret.length < 32) throw new IllegalStateException(
                "JWT secret must be >= 32 bytes, got " + secret.length);
        return NimbusJwtDecoder.withSecretKey(
                new SecretKeySpec(secret, "HmacSHA256")).build();
    }
}
