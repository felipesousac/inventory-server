package com.inventory.server.configuration.security;

import com.inventory.server.client.rediscache.RedisCacheClient;
import com.inventory.server.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final RedisCacheClient redisCacheClient;

    private final UserService userService;

    public JwtInterceptor(RedisCacheClient redisCacheClient, UserService userService) {
        this.redisCacheClient = redisCacheClient;
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = retrieveToken(request);

        if (token != null) {
            /*
                Get token value from principal
            */
            Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwt = (Jwt) authenticationToken.getPrincipal();
            String id = jwt.getClaim("id");

            /*
                Set UserDetail as principal in SecurityContextHolder
                Necessary for repositories' custom queries
             */
            String subject = jwt.getSubject();
            UserDetails user = userService.loadUserByUsername(subject);

            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                    user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (!redisCacheClient.isTokenInWhiteList(id, jwt.getTokenValue())) {
                throw new BadCredentialsException("Invalid token");
            }
        }

        return true;
    }

    private String retrieveToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }

        return null;
    }
}
