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
import org.oportuniza.oportunizabackend.authentication.utils.JwtUtils;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Operation(summary = "Login a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = LoginResponseDTO.class))
            })
    })
    public ResponseEntity<LoginResponseDTO> authenticateAndGetToken(@RequestBody @Valid LoginDTO loginDTO) {
        var emailPassword = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());
        var authentication = authenticationManager.authenticate(emailPassword);

        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.loadUserByUsername(userDetails.getUsername());

            String jwtToken = JwtUtils.generateToken(userDetails);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            var response = new LoginResponseDTO(
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
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = RegisterResponseDTO.class))
            })
    })
    public ResponseEntity<RegisterResponseDTO> createUser(@RequestBody @Valid RegisterDTO registerDTO) {
        if (userService.emailExists(registerDTO.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        User user = userService.createUser(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponseDTO(user.getId(), user.getEmail(), user.getName(), user.getPhoneNumber()));
    }

}

