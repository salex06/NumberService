package github.salex9206.shared.auth.principal;

import github.salex9206.shared.auth.token.UserJwtAuthToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Configuration
public class UserPrincipalConverter {
    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> customJwtAuthenticationConverter() {
        return jwt -> {
            // extract data
            UUID userId = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
            String username = jwt.getClaimAsString("username");
            List<String> roles = jwt.getClaimAsStringList("roles");

            // create user principal
            UserPrincipal principal = new UserPrincipal(
                    userId,
                    username,
                    roles != null ? Set.copyOf(roles) : Set.of()
            );

            // generate authorities
            List<SimpleGrantedAuthority> authorities = (roles != null ? roles : List.<String>of()).stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .toList();

            // create auth token object
            return new UserJwtAuthToken(jwt, authorities, principal);
        };
    }
}
