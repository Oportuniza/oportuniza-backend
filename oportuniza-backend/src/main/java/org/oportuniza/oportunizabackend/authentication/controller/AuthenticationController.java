package org.oportuniza.oportunizabackend.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.LoginResponseDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterResponseDTO;
import org.oportuniza.oportunizabackend.authentication.exceptions.EmailAlreadyExistsException;
import org.oportuniza.oportunizabackend.authentication.utils.JwtUtils;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.oportuniza.oportunizabackend.utils.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthenticationController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Login a user")
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
        // Create authentication token
        var emailPassword = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());

        // Authenticate the user
        var authentication = authenticationManager.authenticate(emailPassword);

        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Authentication failed");
        }
        // If authenticated, generate the JWT token
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.loadUserByUsername(userDetails.getUsername());

        String jwtToken = JwtUtils.generateToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new LoginResponseDTO(
                user.getId(),
                user.getEmail(),
                roles,
                user.getName(),
                user.getPhoneNumber(),
                user.getResumeUrl(),
                user.getAverageRating(),
                user.getReviewCount(),
                user.getDistrict(),
                user.getCounty(),
                jwtToken);
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
    public RegisterResponseDTO createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The details to register a user") @RequestBody @Valid RegisterDTO registerDTO)
            throws EmailAlreadyExistsException {
        if (userService.emailExists(registerDTO.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = userService.createUser(registerDTO);
        return new RegisterResponseDTO(user.getId(), user.getEmail(), user.getName(), user.getPhoneNumber());
    }

    @GetMapping("/loginSuccess")
    public String getLoginInfo(OAuth2AuthenticationToken token) {
        // You can get user info from the token here
        return "loginSuccess";
    }

}

