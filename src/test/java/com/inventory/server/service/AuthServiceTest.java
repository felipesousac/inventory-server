package com.inventory.server.service;

import com.inventory.server.domain.UserRepository;
import com.inventory.server.dto.auth.ChangePasswordData;
import com.inventory.server.infra.exception.PasswordChangeIllegalArgumentException;
import com.inventory.server.model.User;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthService authService;

    @Test
    void changePasswordSuccess() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setPassword("oldPassword");
        ChangePasswordData data = new ChangePasswordData("oldPassword", "123", "123");

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(passwordEncoder.encode(anyString())).willReturn(data.newPassword());

        // When
        authService.changePassword(user.getId(), data);

        // Then
        assertEquals("123", data.newPassword());

    }

    @Test
    void changePasswordOldPasswordIsIncorrect() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setPassword("oldPassword");
        ChangePasswordData data = new ChangePasswordData("oldPasswordIncorrect", "123", "123");

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // When
        Exception ex = assertThrows(BadCredentialsException.class, () -> {
            authService.changePassword(user.getId(), data);
        });

        // Then
        String message = "Old password is incorrect";
        assertThat(ex).isInstanceOf(BadCredentialsException.class).hasMessage(message);
    }

    @Test
    void changePasswordDoNotMatch() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setPassword("oldPassword");
        ChangePasswordData data = new ChangePasswordData("oldPasswordIncorrect", "123", "1234");

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        // When
        Exception ex = assertThrows(PasswordChangeIllegalArgumentException.class, () -> {
            authService.changePassword(user.getId(), data);
        });

        // Then
        String message = "New password and confirm password do not match";
        assertThat(ex).isInstanceOf(PasswordChangeIllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void changePasswordUserNotFound() {
        // Given
        ChangePasswordData data = new ChangePasswordData("oldPasswordIncorrect", "123", "123");

        given(userRepository.findById(1L)).willReturn(Optional.empty());

        // When
        Exception ex = assertThrows(ObjectNotFoundException.class, () -> {
            authService.changePassword(1L, data);
        });

        // Then
        assertThat(ex).isInstanceOf(ObjectNotFoundException.class);
    }
}