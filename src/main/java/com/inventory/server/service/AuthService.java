package com.inventory.server.service;

import com.inventory.server.domain.UserRepository;
import com.inventory.server.dto.auth.AuthRegisterData;
import com.inventory.server.infra.exception.ItemAlreadyCreatedException;
import com.inventory.server.model.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new BadCredentialsException("Wrong username or password");
        }

        return user;
    }

    @Transactional
    public void signUp(AuthRegisterData data) throws Exception {
        User isUserRegistered = userRepository.findByUsername(data.username());

        if (isUserRegistered != null) {
            throw new ItemAlreadyCreatedException("User already registered");
        }


        try {
            User user = new User(data);
            userRepository.save(user);
        } catch (Exception ex) {
            // In case race condition occurs, database will throw error because of unique constraint
            throw new Exception("User not created");
        }
    }
}
