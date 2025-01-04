package com.inventory.server.user.dto;

import com.inventory.server.permission.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserRegisterData(
        @NotBlank
        @Size(max = 30)
        String username,
        @NotBlank
        @Size(min = 6)
        String password,
        @NotBlank
        @Size(min = 6)
        String confirmPassword,
        List<Permission> permissions) {
}
