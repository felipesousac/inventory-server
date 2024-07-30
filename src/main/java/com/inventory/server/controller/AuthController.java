package com.inventory.server.controller;

import com.inventory.server.configuration.tokenConfiguration.TokenJWTData;
import com.inventory.server.configuration.tokenConfiguration.TokenService;
import com.inventory.server.dto.auth.AuthData;
import com.inventory.server.model.User;
import com.inventory.server.serialization.converter.YamlMediaType;
import com.inventory.server.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager manager;

    private final TokenService tokenService;

    private final AuthService authService;

    public AuthController(AuthenticationManager manager, TokenService tokenService, AuthService authService) {
        this.manager = manager;
        this.tokenService = tokenService;
        this.authService = authService;
    }


    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
            YamlMediaType.APPLICATION_YAML},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML})
    public ResponseEntity<TokenJWTData> login(@RequestBody @Valid AuthData data) {
        if (authService.loadUserByUsername(data.username()) == null) {
            throw new BadCredentialsException("Wrong username or password");
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(data.username(), data.userPass());
        Authentication auth = manager.authenticate(authToken);

        String tokenJWT = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new TokenJWTData(tokenJWT));
    }
}
