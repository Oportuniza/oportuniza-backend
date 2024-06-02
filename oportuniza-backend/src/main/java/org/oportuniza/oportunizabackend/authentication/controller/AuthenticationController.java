package org.oportuniza.oportunizabackend.authentication.controller;

import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.authentication.dto.TokenDTO;
import org.oportuniza.oportunizabackend.authentication.service.JwtService;
import org.oportuniza.oportunizabackend.users.dto.LoginDTO;
import org.oportuniza.oportunizabackend.users.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.users.model.Role;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.repository.UserRepository;
import org.oportuniza.oportunizabackend.users.service.DetailsUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserRepository userRepository;

    // handles and verifies user credentials, along with applying authentication rules
    private final AuthenticationManager authenticationManager;

    // provides basic jwt-related features, such as token generation and extraction of fields from jwt
    private final JwtService jwtService;

    private final DetailsUserService userService;
    private final PasswordEncoder passwordEncoder;

    // Dep inj by constructor
    public AuthenticationController(AuthenticationManager authenticationManager, JwtService jwtService, DetailsUserService userService,
                                    PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> authenticateAndGetToken(@RequestBody @Valid LoginDTO loginDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());

        // Use authentication manager to authenticate
        var authentication = authenticationManager.authenticate(usernamePassword);

        if (authentication.isAuthenticated()) {
            // If authentication succeeds, generate the token for the user and provide it in the response
            try {
                var user = userService.loadUserByUsername(loginDTO.email());
                var token = jwtService.generateToken(user);
                return ResponseEntity.ok(new TokenDTO(token));
            } catch (UsernameNotFoundException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody @Valid RegisterDTO registerDTO) {
        // Cannot register two users with the same email
        if (userRepository.findByEmail(registerDTO.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        // Encode password, before sending to the database
        String encryptedPassword = passwordEncoder.encode(registerDTO.password());

        User user = new User();
        user.setEmail(registerDTO.email());
        user.setPassword(encryptedPassword);
        user.addRole(new Role("ROLE_USER"));
        this.userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

}

