package org.oportuniza.oportunizabackend.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.oportuniza.oportunizabackend.TestUtils;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.users.dto.UpdateUserDTO;
import org.oportuniza.oportunizabackend.users.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Test
    public void getUserTest() throws Exception {
        // Create user
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "123456789", "João da Silva");
        var user1 = testUtils.registerUser(registerDTO);
        // Login user
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));
        // Get user
        MvcResult result = mockMvc.perform(get(String.format("/api/users/%d", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        UserDTO response = objectMapper.readValue(content, UserDTO.class);

        assertNotNull(response);
        assertEquals(registerDTO.email(), response.email());
        assertEquals(registerDTO.name(), response.name());
        assertEquals(registerDTO.phoneNumber(), response.phoneNumber());
        assertNull(response.county());
        assertNull(response.district());
    }

    @Test
    public void updateUserTest() throws Exception {
        // Create user
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "123456789", "João da Silva");
        var user1 = testUtils.registerUser(registerDTO);
        // Login user
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));
        // Update user
        var updateUserDTO = new UpdateUserDTO(
                "joao@gmail.com",
                null,
                null,
                "João Candido",
                "987654321",
                null,
                "Viana do Castelo",
                "Ponte de Lima");
        MvcResult result = mockMvc.perform(put(String.format("/api/users/%d", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        UserDTO response = objectMapper.readValue(content, UserDTO.class);

        assertNotNull(response);
        assertEquals(updateUserDTO.email(), response.email());
        assertEquals(updateUserDTO.name(), response.name());
        assertEquals(updateUserDTO.phoneNumber(), response.phoneNumber());
        assertEquals(updateUserDTO.county(), response.county());
        assertEquals(updateUserDTO.district(), response.district());
    }

    @Test
    public void getFavoriteUsersTest() throws Exception {
        // Create Users
        var user1 = testUtils.registerUser(new RegisterDTO("joao@gmail.com", "123456", "123456789", "João da Silva"));
        var user2 = testUtils.registerUser(new RegisterDTO("jose@gmail.com", "123456", "123456789", "José da Silva"));
        var user3 = testUtils.registerUser(new RegisterDTO("joana@gmail.com", "123456", "123456789", "Joana da Silva"));

        // Login User 1
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        // Add favorite users
        mockMvc.perform(patch(String.format("/api/users/%d/favorites/add", loginResponseDTO.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2.id())))
                .andExpect(status().isOk());

        mockMvc.perform(patch(String.format("/api/users/%d/favorites/add", loginResponseDTO.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user3.id())))
                .andExpect(status().isOk());

        // Get favorite users
        MvcResult result = mockMvc.perform(get(String.format("/api/users/%d/favorites", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<UserDTO> response = objectMapper.readValue(content, new TypeReference<List<UserDTO>>() {});

        assertNotNull(response);
        assertEquals(2, response.size());
        for (UserDTO user : response) {
            assertNotNull(user);
            assertNotNull(user.email());
            assertNotNull(user.name());
            assertNotNull(user.phoneNumber());
            assertNull(user.county());
            assertNull(user.district());
            if (user.id() == user2.id()) {
                assertEquals(user.email(), user2.email());
                assertEquals(user.name(), user2.name());
                assertEquals(user.phoneNumber(), user2.phoneNumber());
            } else if (user.id() == user3.id()) {
                assertEquals(user.email(), user3.email());
                assertEquals(user.name(), user3.name());
                assertEquals(user.phoneNumber(), user3.phoneNumber());
            }
        }
    }


    /*
    @Test
    public void shouldRemoveFavoriteUser() throws Exception {
        User user = new User();
        user.setUsername("removablefavoriteuser");
        userService.saveUser(user);  // Assuming you have a saveUser method

        mockMvc.perform(patch("/api/users/1/favorites/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/users/1/favorites/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user.getId())))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldAddFavoriteOffer() throws Exception {
        mockMvc.perform(patch("/api/users/1/favorites/offers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offer.getId())))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldRemoveFavoriteOffer() throws Exception {
        mockMvc.perform(patch("/api/users/1/favorites/offers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offer.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/users/1/favorites/offers/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offer.getId())))
                .andExpect(status().isOk())
                .andReturn();
    }

    */
}
