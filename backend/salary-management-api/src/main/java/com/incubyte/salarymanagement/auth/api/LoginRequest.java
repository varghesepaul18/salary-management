package com.incubyte.salarymanagement.auth.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "HR manager credentials used to obtain a JWT access token")
public record LoginRequest(
        @Schema(description = "Configured HR manager username", example = "hr-manager")
        String username,
        @Schema(description = "Configured HR manager password", accessMode = Schema.AccessMode.WRITE_ONLY)
        String password) {
}
