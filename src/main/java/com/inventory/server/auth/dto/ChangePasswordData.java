package com.inventory.server.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordData(
        @NotBlank
        String oldPassword,
        @NotBlank
        String newPassword,
        @NotBlank
        String confirmNewPassword) {
}
