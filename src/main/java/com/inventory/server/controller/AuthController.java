package com.inventory.server.controller;

import com.inventory.server.dto.AuthData;
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

    public AuthController(AuthenticationManager manager) {
        this.manager = manager;
    }

    @PostMapping
    public ResponseEntity login(@RequestBody @Valid AuthData data) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(data.username(), data.userPass());
        Authentication auth = manager.authenticate(token);

        return ResponseEntity.ok().build();
    }
}
