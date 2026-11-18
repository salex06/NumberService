package github.salex9206.shared.auth.token.properties;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;

@ConfigurationProperties(prefix = "app.jwt")
@Getter @Setter @Validated @Slf4j
public class JwtProperties {
    @NotBlank @Size(min = 64)
    private String secretKey;
    private Duration validity;

    @PostConstruct
    void logFingerprint() throws Exception {
        byte[] key = HexFormat.of().parseHex(secretKey);
        log.info("JWT key fingerprint: {}, bytes={}",
                HexFormat.of().formatHex(
                        MessageDigest.getInstance("SHA-256").digest(key)).substring(0, 16),
                key.length);
    }
}
