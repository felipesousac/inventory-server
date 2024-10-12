package com.inventory.server.service;

import com.inventory.server.client.rediscache.RedisCacheClient;
import com.inventory.server.domain.PermissionRepository;
import com.inventory.server.domain.UserRepository;
import com.inventory.server.dto.auth.AuthRegisterData;
import com.inventory.server.dto.auth.ChangePasswordData;
import com.inventory.server.infra.exception.PasswordChangeIllegalArgumentException;
import com.inventory.server.infra.exception.UserAlreadyRegisteredException;
import com.inventory.server.model.Permission;
import com.inventory.server.model.User;
import org.hibernate.ObjectNotFoundException;
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
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Wrong username or password"));
    }

    @Transactional
    public void signUp(AuthRegisterData data) throws Exception {
        Boolean isUserRegistered = userRepository.existsByUsername(data.username());

        if (isUserRegistered) {
            throw new UserAlreadyRegisteredException("User already registered");
        }

        try {
            User user = new User(data);

            Permission permission = permissionRepository.findByDescription("COMMON_USER")
                    .orElseThrow(() -> new ObjectNotFoundException("permission", (Object) "COMMON_USER"));

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
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ObjectNotFoundException("user", userId));

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
}
