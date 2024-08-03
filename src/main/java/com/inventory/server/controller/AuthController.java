package com.inventory.server.controller;

import com.inventory.server.configuration.tokenConfiguration.TokenJWTData;
import com.inventory.server.configuration.tokenConfiguration.TokenService;
import com.inventory.server.dto.auth.AuthData;
import com.inventory.server.model.User;
import com.inventory.server.serialization.converter.YamlMediaType;
import com.inventory.server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
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
@Tag(name = "Authentication", description = "Endpoints for managing authentication")
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
    @Operation(
            summary = "Login request",
            description = "Authenticates an user by passing valid credentials in a in a JSON, XML or YAML representation",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(
                            description = "Ok",
                            responseCode = "200",
                            content = @Content(schema =
                            @Schema(implementation = TokenJWTData.class))),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "401",
                            content = @Content(schema =
                            @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
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
