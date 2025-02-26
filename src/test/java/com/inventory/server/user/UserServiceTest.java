package com.inventory.server.user;

import com.inventory.server.infra.exception.ObjectNotFoundException;
import com.inventory.server.infra.exception.UsernameChangeIllegalArgumentException;
import com.inventory.server.user.dto.ChangePasswordData;
import com.inventory.server.client.rediscache.RedisCacheClient;
import com.inventory.server.infra.exception.PasswordChangeIllegalArgumentException;
import com.inventory.server.user.dto.UsernameChangeData;
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
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RedisCacheClient redisCacheClient;

    @InjectMocks
    UserService userService;

    @Test
    void changePasswordSuccess() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setPassword("oldPassword");
        ChangePasswordData data = new ChangePasswordData("oldPassword", "123", "123");

        given(userRepository.findByIdAndEnabledTrue(user.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(passwordEncoder.encode(anyString())).willReturn(data.newPassword());
        doNothing().when(redisCacheClient).delete(anyString());

        // When
        userService.changePassword(user.getId(), data);

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

        given(userRepository.findByIdAndEnabledTrue(user.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // When
        Exception ex = assertThrows(BadCredentialsException.class, () -> {
            userService.changePassword(user.getId(), data);
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

        given(userRepository.findByIdAndEnabledTrue(user.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        // When
        Exception ex = assertThrows(PasswordChangeIllegalArgumentException.class, () -> {
            userService.changePassword(user.getId(), data);
        });

        // Then
        String message = "New password and confirm password do not match";
        assertThat(ex).isInstanceOf(PasswordChangeIllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void changePasswordUserNotFound() {
        // Given
        ChangePasswordData data = new ChangePasswordData("oldPasswordIncorrect", "123", "123");

        given(userRepository.findByIdAndEnabledTrue(1L)).willReturn(Optional.empty());

        // When
        Exception ex = assertThrows(ObjectNotFoundException.class, () -> {
            userService.changePassword(1L, data);
        });

        // Then
        assertThat(ex).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void changeUsernameSuccess() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("oldUsername");
        UsernameChangeData data = new UsernameChangeData("newUsername", "newUsername");

        given(userRepository.findByIdAndEnabledTrue(user.getId())).willReturn(Optional.of(user));
        doNothing().when(redisCacheClient).delete(anyString());

        // When
        userService.changeUsername(user.getId(), data);

        // Then
        assertEquals("newUsername", data.newUsername());
    }

    @Test
    void changeUsernameDoNotMatch() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("oldUsername");
        UsernameChangeData data = new UsernameChangeData("newUsername", "Username");

        given(userRepository.findByIdAndEnabledTrue(user.getId())).willReturn(Optional.of(user));

        // When
        Exception ex = assertThrows(UsernameChangeIllegalArgumentException.class, () -> {
            userService.changeUsername(user.getId(), data);
        });

        // Then
        String message = "New Username and Confirm New Username do not match";
        assertThat(ex).isInstanceOf(UsernameChangeIllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void changeUsernameUserNotFound() {
        // Given
        UsernameChangeData data = new UsernameChangeData("newUsername", "Username");

        given(userRepository.findByIdAndEnabledTrue(1L)).willReturn(Optional.empty());

        // When
        Exception ex = assertThrows(ObjectNotFoundException.class, () -> {
            userService.changeUsername(1L, data);
        });

        // Then
        assertThat(ex).isInstanceOf(ObjectNotFoundException.class);
    }
}