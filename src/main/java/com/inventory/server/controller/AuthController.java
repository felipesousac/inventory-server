package com.inventory.server.controller;

import com.inventory.server.configuration.tokenConfiguration.TokenJWTData;
import com.inventory.server.configuration.tokenConfiguration.TokenService;
import com.inventory.server.dto.auth.AuthData;
import com.inventory.server.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

    public AuthController(AuthenticationManager manager, TokenService tokenService) {
        this.manager = manager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity login(@RequestBody @Valid AuthData data) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(data.username(), data.userPass());
        Authentication auth = manager.authenticate(authToken);

        String tokenJWT = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new TokenJWTData(tokenJWT));
    }
}
