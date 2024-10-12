package com.inventory.server.configuration.security;

import com.inventory.server.client.rediscache.RedisCacheClient;
import com.inventory.server.configuration.tokenConfiguration.TokenService;
import com.inventory.server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final RedisCacheClient redisCacheClient;

    private final JwtDecoder jwtDecoder;

    private final TokenService tokenService;

    private final UserService userService;

    public JwtInterceptor(RedisCacheClient redisCacheClient, JwtDecoder jwtDecoder, TokenService tokenService, UserService userService) {
        this.redisCacheClient = redisCacheClient;
        this.jwtDecoder = jwtDecoder;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = retrieveToken(request);

        if (token != null) {
            // Get token value from principal
            Authentication authenticationTest = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwt = (Jwt) authenticationTest.getPrincipal();
            String id = jwt.getId();

            /*
                Set UserDetail as principal in SecurityContextHolder
                Necessary for repository's custom queries
             */
            String subject = tokenService.getSubject(token);
            //String subject = jwt.getSubject();
            UserDetails user = userService.loadUserByUsername(subject);

            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                    user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get id from bearer token to verify in redis white list
            //Jwt decode = jwtDecoder.decode(token);
            //String id = (String) decode.getClaim("id");

            if (!redisCacheClient.isTokenInWhiteList(id, token)) {
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
