package org.oportuniza.oportunizabackend.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.authentication.dto.*;
import org.oportuniza.oportunizabackend.authentication.exceptions.EmailAlreadyExistsException;
import org.oportuniza.oportunizabackend.authentication.utils.AuthenticationUtils;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.oportuniza.oportunizabackend.utils.ErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthenticationController(@Value("${spring.security.oauth2.client.registration.google.client-id}")String clientId, AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Login a user with email and password and return a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = LoginResponseDTO.class))
            }),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public LoginResponseDTO authenticateAndGetToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The email and password to login a user") @RequestBody @Valid LoginDTO loginDTO)
            throws AuthenticationException {
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password()));

        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Authentication failed");
        }
        // If authenticated, generate the JWT token
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.loadUserByUsername(userDetails.getUsername());
        user.setLastActivityAt(new Date());
        userService.save(user);

        return AuthenticationUtils.buildLoginResponse(user);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = RegisterResponseDTO.class))
            }),
            @ApiResponse(responseCode = "409", description = "Email already exists", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public LoginResponseDTO createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The details to register a user") @RequestBody @Valid RegisterDTO registerDTO)
            throws EmailAlreadyExistsException {
        if (userService.emailExists(registerDTO.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = userService.createUser(registerDTO);
        // Authenticate the user
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(registerDTO.email(), registerDTO.password()));

        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Authentication failed");
        }
        // If authenticated, generate the JWT token
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return AuthenticationUtils.buildLoginResponse(user);
    }

    @PostMapping("/google")
    public LoginResponseDTO googleAuth(@RequestBody GoogleDTO googleDTO) throws MalformedURLException, URISyntaxException, EmailAlreadyExistsException {

        User user;
        if (!userService.emailExists(googleDTO.email())) {
            user = userService.createUser(googleDTO);
        } else {
            user = userService.loadUserByUsername(googleDTO.email());
            if (!user.getAuthProvider().equals("google")) {
                throw new EmailAlreadyExistsException("Email already exists with different provider");
            }
        }
        return AuthenticationUtils.buildLoginResponse(user);
    }

}

