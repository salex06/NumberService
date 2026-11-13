package github.salex9206.shared.auth.token;

import github.salex9206.shared.auth.principal.UserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

public class UserJwtAuthToken extends JwtAuthenticationToken {

    private final UserPrincipal principal;

    public UserJwtAuthToken(Jwt jwt,
                            Collection<? extends GrantedAuthority> authorities,
                            UserPrincipal principal) {
        super(jwt, authorities);
        this.principal = principal;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public String getName() {
        return principal.username();
    }
}

