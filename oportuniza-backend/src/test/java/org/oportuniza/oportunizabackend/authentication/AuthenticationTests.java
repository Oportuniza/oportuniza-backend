package org.oportuniza.oportunizabackend.authentication;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.LoginResponseDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterResponseDTO;
import org.oportuniza.oportunizabackend.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void registerAndLoginUserTest() throws Exception {
        // Register user
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "123456789", "Joao");
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        // Capture and parse the register response
        String registerResponseContent = registerResult.getResponse().getContentAsString();
        RegisterResponseDTO registerResponse = objectMapper.readValue(registerResponseContent, RegisterResponseDTO.class);

        // Validate register response fields
        assertNotNull(registerResponse.email());
        assertNotNull(registerResponse.name());
        assertNotNull(registerResponse.phoneNumber());
        assertEquals(registerDTO.email(), registerResponse.email());
        assertEquals(registerDTO.name(), registerResponse.name());
        assertEquals(registerDTO.phoneNumber(), registerResponse.phoneNumber());

        // Login user
        var loginDTO = new LoginDTO("joao@gmail.com", "123456");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        // Capture and parse the login response
        String loginResponseContent = loginResult.getResponse().getContentAsString();
        LoginResponseDTO loginResponse = objectMapper.readValue(loginResponseContent, LoginResponseDTO.class);

        // Validate login response fields
        assertNotNull(loginResponse.jwtToken());
        assertNotNull(loginResponse.email());
        assertNotNull(loginResponse.id());
        assertNotNull(loginResponse.roles());
        assertEquals(registerDTO.email(), loginResponse.email());
        assertEquals(registerResponse.id(), loginResponse.id());
        assertEquals(1, loginResponse.roles().size());
        assertEquals("ROLE_USER", loginResponse.roles().getFirst());

        userRepository.deleteById(registerResponse.id());
    }



}
