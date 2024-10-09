package com.inventory.server.configuration.security;

import com.inventory.server.client.rediscache.RedisCacheClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final RedisCacheClient redisCacheClient;

    private final JwtDecoder jwtDecoder;

    public JwtInterceptor(RedisCacheClient redisCacheClient, JwtDecoder jwtDecoder) {
        this.redisCacheClient = redisCacheClient;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null) {
            String token = authorizationHeader.replace("Bearer ", "");

            Jwt decode = jwtDecoder.decode(token);

            String id = (String) decode.getClaim("id");

            if (!redisCacheClient.isTokenInWhiteList(id, token)) {
                throw new BadCredentialsException("Invalid token");
            }
        }

        return true;
    }
}
