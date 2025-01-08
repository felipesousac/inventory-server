package com.inventory.server.user;

import com.inventory.server.user.dto.UserRegisterData;
import com.inventory.server.user.dto.ChangePasswordData;
import com.inventory.server.serialization.converter.YamlMediaType;
import com.inventory.server.user.dto.UsernameChangeData;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML})
    @Operation(
            summary = "Sign up new user",
            description = "Register a new user by passing valid data in a JSON, XML or YAML " +
                    "representation",
            tags = {"Users"},
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
    public ResponseEntity<?> signUp(@RequestBody @Valid UserRegisterData data) throws Exception {
        userService.signUp(data);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping(
            value = "/{userId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML}
    )
    @Operation(
            summary = "Changes a user password",
            description = "Allows a user to change its password and ADMINS can change any user password",
            tags = {"Users"},
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
        userService.changePassword(userId, data);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{userId}/username")
    @Operation(
            summary = "Changes a user name",
            description = "Allows a user to change its name and ADMINS can change any user name",
            tags = {"Users"},
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
    public ResponseEntity<?> changeUsername(@PathVariable Long userId,
                                            @RequestBody @Valid UsernameChangeData data) {
        userService.changeUsername(userId, data);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Operation(
            summary = "Deletes user",
            description = "Allows an user to soft delete its own account",
            tags = {"Users"},
            responses = {
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
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
    public ResponseEntity<?> deleteUser() {
        userService.deleteUser();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
