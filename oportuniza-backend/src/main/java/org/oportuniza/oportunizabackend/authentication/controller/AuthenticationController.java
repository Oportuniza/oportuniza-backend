package org.oportuniza.oportunizabackend.authentication.controller;

import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.authentication.dto.TokenDTO;
import org.oportuniza.oportunizabackend.authentication.service.JwtService;
import org.oportuniza.oportunizabackend.users.dto.LoginUserDTO;
import org.oportuniza.oportunizabackend.users.dto.RegisterUserDTO;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.service.DetailsUserService;
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
    private final DetailsUserService detailsUserService;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtService jwtService, UserService userService,
                                    DetailsUserService detailsUserService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.detailsUserService = detailsUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> authenticateAndGetToken(@RequestBody @Valid LoginUserDTO loginUserDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginUserDTO.email(), loginUserDTO.password());
        var authentication = authenticationManager.authenticate(usernamePassword);

        if (authentication.isAuthenticated()) {
            var user = detailsUserService.loadUserByUsername(loginUserDTO.email());
            var token = jwtService.generateToken(user);
            return ResponseEntity.ok(new TokenDTO(user.getId(), token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody @Valid RegisterUserDTO registerUserDTO) {
        if (userService.emailExists(registerUserDTO.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        User user = userService.createUser(registerUserDTO); // change this
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

}

