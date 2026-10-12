package com.incubyte.salarymanagement.auth.api;

import com.incubyte.salarymanagement.auth.application.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "HR manager authentication and token issuance")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Sign in as an HR manager", description = "Validates the configured HR manager account and returns a short-lived JWT access token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credentials accepted; JWT issued",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Malformed login request"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    public LoginResponse login(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "HR manager credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class)))
            LoginRequest request) {
        try {
            log.info("Login attempt for user: {}", request.username());
            return authService.login(request.username(), request.password());
        } catch (AuthenticationException exception) {
            log.warn("Login failed for user: {}", request.username());
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }
}
