package com.inventory.server.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginData(
        @NotBlank
        String username,
        @NotBlank
        String password) {
}
