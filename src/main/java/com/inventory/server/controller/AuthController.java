package com.inventory.server.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.inventory.server.client.rediscache.RedisCacheClient;
import com.inventory.server.configuration.tokenConfiguration.TokenJWTData;
import com.inventory.server.configuration.tokenConfiguration.TokenService;
import com.inventory.server.configuration.tokenConfiguration.TokensData;
import com.inventory.server.dto.auth.AuthLoginData;
import com.inventory.server.dto.auth.AuthRegisterData;
import com.inventory.server.dto.auth.ChangePasswordData;
import com.inventory.server.model.User;
import com.inventory.server.serialization.converter.YamlMediaType;
import com.inventory.server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for managing authentication")
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager manager;

    private final TokenService tokenService;

    private final RedisCacheClient redisCacheClient;

    public AuthController(AuthService authService, AuthenticationManager manager, TokenService tokenService, RedisCacheClient redisCacheClient) {
        this.authService = authService;
        this.manager = manager;
        this.tokenService = tokenService;
        this.redisCacheClient = redisCacheClient;
    }

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
            YamlMediaType.APPLICATION_YAML},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML})
    @Operation(
            summary = "Login request",
            description = "Authenticates an user by passing valid credentials in a JSON, XML or YAML representation",
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
    public ResponseEntity<TokensData> login(@RequestBody @Valid AuthLoginData data) {
        UserDetails user = authService.loadUserByUsername(data.username());
        List<String> roles =
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());

        Authentication auth = manager.authenticate(authToken);

        TokensData tokenResponse = tokenService.createAccessToken(data.username(), roles, (User) auth.getPrincipal());
        DecodedJWT decodedJWT = tokenService.decodedToken(tokenResponse.accessToken());

        redisCacheClient.set("whitelist:" + ((User) auth.getPrincipal()).getId(),
                tokenResponse.accessToken(), 2, TimeUnit.HOURS);

        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping(
            value = "/signup",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML})
    @Operation(
            summary = "Sign up new user",
            description = "Register a new user by passing valid data in a JSON, XML or YAML " +
                    "representation",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
                    @ApiResponse(
                            description = "Bad request",
                            responseCode = "400",
                            content = @Content(
                                    schema = @Schema(implementation = ProblemDetail.class)
                            ))
            }
    )
    public ResponseEntity<?> signUp(@RequestBody @Valid AuthRegisterData data) throws Exception {
        authService.signUp(data);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping(
            value = "/users/{userId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML}
    )
    @Operation(
            summary = "Changes a user password",
            description = "Allows a user to change its password and ADMINS can change any user password",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
                    @ApiResponse(
                            description = "Bad request",
                            responseCode = "400",
                            content = @Content(
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )),
                    @ApiResponse(
                            description = "No Content",
                            responseCode = "204",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "401",
                            content = @Content(
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> changePassword(@PathVariable Long userId,
                                            @RequestBody @Valid ChangePasswordData data) {
        authService.changePassword(userId, data);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
