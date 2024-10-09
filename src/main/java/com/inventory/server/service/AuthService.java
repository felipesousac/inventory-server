package com.inventory.server.service;

import com.inventory.server.client.rediscache.RedisCacheClient;
import com.inventory.server.configuration.tokenConfiguration.TokenService;
import com.inventory.server.configuration.tokenConfiguration.TokensData;
import com.inventory.server.dto.auth.AuthLoginData;
import com.inventory.server.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
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

        //TokensData tokenResponse = tokenService.createAccessToken(data.username(), roles,
         //       (User) auth.getPrincipal());

        //DecodedJWT decodedJWT = tokenService.decodedToken(tokenResponse.accessToken());

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
