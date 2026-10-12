package com.incubyte.salarymanagement.auth.api;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds) {
}