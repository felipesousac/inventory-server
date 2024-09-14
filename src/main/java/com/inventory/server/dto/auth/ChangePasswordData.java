package com.inventory.server.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordData(
        @NotBlank
        String oldPassword,
        @NotBlank
        String newPassword,
        @NotBlank
        String confirmNewPassword) {
}
