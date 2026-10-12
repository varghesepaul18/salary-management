package com.incubyte.salarymanagement.auth.application;

import com.incubyte.salarymanagement.auth.api.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-12T12:00:00Z");

    private AuthenticationManager authenticationManager;
    private JwtEncoder jwtEncoder;
    private AuthService service;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtEncoder = mock(JwtEncoder.class);
        service = new AuthService(
                authenticationManager,
                jwtEncoder,
                Clock.fixed(NOW, ZoneOffset.UTC),
                30);
    }

    @Test
    void authenticatesUserAndIssuesJwtWithExpectedClaims() {
        Authentication authenticatedUser =
                UsernamePasswordAuthenticationToken.authenticated(
                        "hr-manager",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_HR_MANAGER")));

        Jwt jwt = Jwt.withTokenValue("test.jwt.token")
                .header("alg", "HS256")
                .subject("hr-manager")
                .issuedAt(NOW)
                .expiresAt(NOW.plusSeconds(1800))
                .build();

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authenticatedUser);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        LoginResponse response = service.login("hr-manager", "demo-password");

        assertEquals("test.jwt.token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(1800, response.expiresInSeconds());

        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void rejectsBlankCredentialsWithoutAuthenticating() {
        assertThrows(
                BadCredentialsException.class,
                () -> service.login(" ", "demo-password"));

        verifyNoInteractions(authenticationManager, jwtEncoder);
    }

    @Test
    void doesNotIssueJwtWhenAuthenticationFails() {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> service.login("hr-manager", "wrong-password"));

        verifyNoInteractions(jwtEncoder);
    }
}