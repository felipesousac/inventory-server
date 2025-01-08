package com.inventory.server.auth;

import com.inventory.server.client.rediscache.RedisCacheClient;
import com.inventory.server.configuration.tokenConfiguration.TokenService;
import com.inventory.server.auth.dto.AuthLoginData;
import com.inventory.server.user.User;
import com.inventory.server.user.UserService;
import io.micrometer.observation.annotation.Observed;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Observed(name = "authService")
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager manager;
    private final TokenService tokenService;
    private final RedisCacheClient redisCacheClient;

    public AuthService(UserService userService, AuthenticationManager manager, TokenService tokenService,
                       RedisCacheClient redisCacheClient) {
        this.userService = userService;
        this.manager = manager;
        this.tokenService = tokenService;
        this.redisCacheClient = redisCacheClient;
    }

    public Map<String, String> login(AuthLoginData data) {
        UserDetails user = userService.loadUserByUsername(data.username());

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());

        Authentication auth = manager.authenticate(authToken);

        String tokenResponse = tokenService.createToken(auth);

        redisCacheClient.set(
                "whitelist:" + ((User) auth.getPrincipal()).getId(),
                tokenResponse,
                2,
                TimeUnit.HOURS);

        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("token", tokenResponse);

        return tokenData;
    }
}
