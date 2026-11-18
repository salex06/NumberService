package github.salex9206.user.auth.service;

import github.salex9206.shared.auth.token.properties.JwtProperties;
import github.salex9206.user.auth.api.dto.TokenRequest;
import github.salex9206.user.auth.api.dto.TokenResponse;
import github.salex9206.user.auth.repository.UserRepository;
import github.salex9206.user.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    public TokenResponse generateToken(TokenRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        var user = userRepository.findUserByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getUsername()));

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("roles", List.of());
        // TODO: claims.put("roles", user.getRoles().stream().map(Role::getName).toList());

        String token = jwtService.createToken(
            claims,
            String.valueOf(user.getId())
        );

        return TokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getValidity().getSeconds())
                .build();
    }
}
