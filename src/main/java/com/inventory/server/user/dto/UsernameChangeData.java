package com.inventory.server.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UsernameChangeData(
        @NotBlank
        String newUsername,
        @NotBlank
        String confirmNewUsername) {
}
