package com.inventory.server.user;

import com.inventory.server.client.rediscache.RedisCacheClient;
import com.inventory.server.infra.exception.ObjectNotFoundException;
import com.inventory.server.infra.exception.UsernameChangeIllegalArgumentException;
import com.inventory.server.permission.PermissionRepository;
import com.inventory.server.user.dto.UserRegisterData;
import com.inventory.server.user.dto.ChangePasswordData;
import com.inventory.server.infra.exception.PasswordChangeIllegalArgumentException;
import com.inventory.server.infra.exception.UserAlreadyRegisteredException;
import com.inventory.server.permission.Permission;
import com.inventory.server.user.dto.UsernameChangeData;
import io.micrometer.observation.annotation.Observed;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Observed(name = "userService")
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PermissionRepository permissionRepository;

    private final PasswordEncoder passwordEncoder;

    private final RedisCacheClient redisCacheClient;

    public UserService(UserRepository userRepository, PermissionRepository permissionRepository,
                       @Lazy PasswordEncoder passwordEncoder, RedisCacheClient redisCacheClient) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisCacheClient = redisCacheClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new BadCredentialsException("Wrong username or password"));
    }

    @Transactional
    public void signUp(UserRegisterData data) throws Exception {
        if (!data.password().equals(data.confirmPassword())) {
            throw new PasswordChangeIllegalArgumentException("Password and confirm password do not " +
                    "match");
        }

        Boolean isUserRegistered = userRepository.existsByUsernameAndEnabledTrue(data.username());

        if (isUserRegistered) {
            throw new UserAlreadyRegisteredException("User already registered");
        }

        try {
            User user = new User(data);

            Permission permission = permissionRepository.findByDescription("COMMON_USER")
                    .orElseThrow(() -> new ObjectNotFoundException("Permission", "COMMON_USER"));

            user.setPermissions(Collections.singletonList(permission));
            userRepository.save(user);
        } catch (Exception ex) {
            //In case race condition occurs, database will throw error because of unique constraint
            //throw new UserAlreadyRegisteredException("User not created");
            throw new Exception(ex);
        }
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordData data) {
        User user = userRepository.findByIdAndEnabledTrue(userId)
                        .orElseThrow(() -> new ObjectNotFoundException(userId, "User"));

        if (!passwordEncoder.matches(data.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Old password is incorrect");
        }

        if (!data.newPassword().equals(data.confirmNewPassword())) {
            throw new PasswordChangeIllegalArgumentException("New password and confirm password do not " +
                    "match");
        }

        redisCacheClient.delete("whitelist:" + userId);
        user.setPassword(passwordEncoder.encode(data.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void changeUsername(Long userId, UsernameChangeData data) {
        User user = userRepository.findByIdAndEnabledTrue(userId)
                .orElseThrow(() -> new ObjectNotFoundException(userId, "User"));

        if (!data.newUsername().equals(data.confirmNewUsername())) {
            throw new UsernameChangeIllegalArgumentException("New Username and Confirm New Username do not " +
                    "match");
        }

        redisCacheClient.delete("whitelist:" + userId);
        user.setUsername(data.newUsername());
        userRepository.save(user);
    }
}
