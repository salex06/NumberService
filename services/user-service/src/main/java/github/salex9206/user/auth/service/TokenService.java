package github.salex9206.user.auth.service;

import github.salex9206.user.auth.api.dto.TokenRequest;
import github.salex9206.user.auth.api.dto.TokenResponse;
import github.salex9206.user.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public TokenResponse generateToken(TokenRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        String token = jwtService.createToken(Collections.emptyMap(), Objects.requireNonNull(userDetails).getUsername()); // TODO empty map?

        return TokenResponse.builder()
                .token(token)
                .build();
    }
}
