package com.inventory.server.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginData(
        @NotBlank
        String username,
        @NotBlank
        String password) {
}
