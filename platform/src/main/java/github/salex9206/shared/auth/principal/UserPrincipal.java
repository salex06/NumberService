package github.salex9206.shared.auth.principal;

import java.util.Set;
import java.util.UUID;

public record UserPrincipal(
        UUID userId,
        String username,
        Set<String> roles
) {}
