package org.oportuniza.oportunizabackend.authentication.controller;

import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.authentication.dto.TokenDTO;
import org.oportuniza.oportunizabackend.authentication.service.JwtService;
import org.oportuniza.oportunizabackend.users.dto.LoginDTO;
import org.oportuniza.oportunizabackend.users.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> authenticateAndGetToken(@RequestBody @Valid LoginDTO loginDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());
        var authentication = authenticationManager.authenticate(usernamePassword);

        if (authentication.isAuthenticated()) {
            var user = userService.loadUserByUsername(loginDTO.email());
            var token = jwtService.generateToken(user);
            return ResponseEntity.ok(new TokenDTO(token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody @Valid RegisterDTO registerDTO) {
        if (userService.emailExists(registerDTO.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        User user = userService.createUser(registerDTO); // change this
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

}

