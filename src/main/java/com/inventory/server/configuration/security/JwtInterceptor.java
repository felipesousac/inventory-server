package com.inventory.server.configuration.security;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.inventory.server.client.rediscache.RedisCacheClient;
import com.inventory.server.configuration.tokenConfiguration.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final RedisCacheClient redisCacheClient;

    private final TokenService tokenService;

    public JwtInterceptor(RedisCacheClient redisCacheClient, TokenService tokenService) {
        this.redisCacheClient = redisCacheClient;
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null) {
            String token = authorizationHeader.replace("Bearer ", "");

            DecodedJWT decodedJWT = tokenService.decodedToken(token);
            Claim id = decodedJWT.getClaims().get("id");

            if (!redisCacheClient.isTokenInWhiteList(String.valueOf(id), token)) {
                throw new BadCredentialsException("Invalid token");
            }
        }

        return true;
    }
}
