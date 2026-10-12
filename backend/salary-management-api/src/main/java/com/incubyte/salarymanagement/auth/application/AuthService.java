package com.incubyte.salarymanagement.auth.application;

import com.incubyte.salarymanagement.auth.api.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final Clock clock;
    private final long expirationMinutes;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            Clock clock,
            @Value("${app.security.jwt.expiration-minutes}") long expirationMinutes) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.clock = clock;
        this.expirationMinutes = expirationMinutes;
    }

    public LoginResponse login(String username, String password) {
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            log.warn("Login rejected: username or password was blank");
            throw new BadCredentialsException("Invalid username or password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(Duration.ofMinutes(expirationMinutes));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("salary-management-api")
                .subject(authentication.getName())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("roles", authentication.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .toList())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)).getTokenValue();

        long expiresInSeconds = Duration.between(issuedAt, expiresAt).toSeconds();
        log.info("Issued JWT for principal={} expiresInSeconds={}",
                authentication.getName(), expiresInSeconds);

        return new LoginResponse(
                token,
                "Bearer",
                expiresInSeconds);
    }
}
