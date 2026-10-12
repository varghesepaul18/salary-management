package com.incubyte.salarymanagement.auth.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT access token issued after successful authentication")
public record LoginResponse(
        @Schema(description = "Signed bearer access token")
        String accessToken,
        @Schema(description = "Token authentication scheme", example = "Bearer")
        String tokenType,
        @Schema(description = "Token lifetime in seconds", example = "1800")
        long expiresInSeconds) {
}
