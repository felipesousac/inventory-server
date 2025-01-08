package com.inventory.server.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterData(
        @NotBlank
        @Size(max = 30)
        String username,
        @Size(min = 6)
        String password,
        @Size(min = 6)
        String confirmPassword) {
}
