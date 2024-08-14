package com.inventory.server.dto.auth;

import com.inventory.server.model.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AuthRegisterData(
        @NotBlank
        @Size(max = 30)
        String username,
        @NotBlank
        @Size(min = 6)
        String password,
        List<Permission> permissions) {
}
