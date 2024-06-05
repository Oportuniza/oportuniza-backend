package org.oportuniza.oportunizabackend.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.oportuniza.oportunizabackend.TestUtils;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.offers.dto.CreateServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.OfferDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.repository.OfferRepository;
import org.oportuniza.oportunizabackend.users.dto.RequestDTO;
import org.oportuniza.oportunizabackend.users.dto.UpdateUserDTO;
import org.oportuniza.oportunizabackend.users.dto.UserDTO;
import org.oportuniza.oportunizabackend.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OfferRepository offerRepository;

    @Test
    public void getUserTest() throws Exception {
        // Create user
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "123456789", "Joao da Silva");
        var user1 = testUtils.registerUser(registerDTO);
        // Login user
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));
        // Get user
        MvcResult result = mockMvc.perform(get(String.format("/api/users/%d", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        UserDTO response = objectMapper.readValue(content, UserDTO.class);

        assertNotNull(response);
        assertEquals(registerDTO.email(), response.email());
        assertEquals(registerDTO.name(), response.name());
        assertEquals(registerDTO.phoneNumber(), response.phoneNumber());
        assertNull(response.county());
        assertNull(response.district());

        userRepository.deleteById(user1.id());
    }

    @Test
    public void updateUserTest() throws Exception {
        // Create user
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "123456789", "Joao da Silva");
        var user1 = testUtils.registerUser(registerDTO);
        // Login user
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));
        // Update user
        var updateUserDTO = new UpdateUserDTO(
                "joao@gmail.com",
                null,
                null,
                "Joao Candido",
                "987654321",
                null,
                "Viana do Castelo",
                "Ponte de Lima");
        MvcResult result = mockMvc.perform(put(String.format("/api/users/%d", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        UserDTO response = objectMapper.readValue(content, UserDTO.class);

        assertNotNull(response);
        assertEquals(updateUserDTO.email(), response.email());
        assertEquals(updateUserDTO.name(), response.name());
        assertEquals(updateUserDTO.phoneNumber(), response.phoneNumber());
        assertEquals(updateUserDTO.county(), response.county());
        assertEquals(updateUserDTO.district(), response.district());

        userRepository.deleteById(user1.id());
    }

    @Test
    public void favoriteUsersTest() throws Exception {
        // Create Users
        var user1 = testUtils.registerUser(new RegisterDTO("joao@gmail.com", "123456", "123456789", "Joao da Silva"));
        var user2 = testUtils.registerUser(new RegisterDTO("jose@gmail.com", "123456", "123456789", "Jose da Silva"));
        var user3 = testUtils.registerUser(new RegisterDTO("joana@gmail.com", "123456", "123456789", "Joana da Silva"));

        // Login User 1
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        // Add favorite users
        mockMvc.perform(patch(String.format("/api/users/%d/favorites/add", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RequestDTO(user2.id()))))
                .andExpect(status().isOk());

        mockMvc.perform(patch(String.format("/api/users/%d/favorites/add", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RequestDTO(user3.id()))))
                .andExpect(status().isOk());

        // Get favorite users
        MvcResult result = mockMvc.perform(get(String.format("/api/users/%d/favorites", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<UserDTO> response = objectMapper.readValue(content, new TypeReference<>() {});

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

        // Remove favorite user
        mockMvc.perform(patch(String.format("/api/users/%d/favorites/remove", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RequestDTO(user2.id()))))
                .andExpect(status().isOk());

        // Get favorite users
        result = mockMvc.perform(get(String.format("/api/users/%d/favorites", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        out.println(content);
        response = objectMapper.readValue(content, new TypeReference<List<UserDTO>>() {});
        out.println(response);

        assertNotNull(response);
        assertEquals(1, response.size());
        for (UserDTO user : response) {
            assertNotNull(user);
            assertNotNull(user.email());
            assertNotNull(user.name());
            assertNotNull(user.phoneNumber());
            assertNull(user.county());
            assertNull(user.district());
            assertEquals(user.id(), user3.id());
            assertEquals(user.email(), user3.email());
            assertEquals(user.name(), user3.name());
            assertEquals(user.phoneNumber(), user3.phoneNumber());
        }

        userRepository.deleteById(user1.id());
        userRepository.deleteById(user2.id());
        userRepository.deleteById(user3.id());
    }

    @Test
    public void favoriteOffersTest() throws Exception {
        // Create Users
        var user1 = testUtils.registerUser(new RegisterDTO("joao@gmail.com", "123456", "123456789", "Joao da Silva"));
        var user2 = testUtils.registerUser(new RegisterDTO("jose@gmail.com", "123456", "123456789", "Jose da Silva"));

        // Login User 1
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        // Create Offer
        var createServiceDTO = new CreateServiceDTO("Limpador de Carros",
                "Limpo carros de luxo por dentro e por fora!",
                false, 200);

        MvcResult result = mockMvc.perform(post(String.format("/api/services/users/%d", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createServiceDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ServiceDTO serviceDTO = objectMapper.readValue(content, ServiceDTO.class);

        // Login User 2
        var loginResponseDTO2 = testUtils.loginUser(new LoginDTO("jose@gmail.com", "123456"));

        // Add favorite offer
        mockMvc.perform(patch(String.format("/api/users/%d/favorites/offers/add", loginResponseDTO2.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO2.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RequestDTO(serviceDTO.getId())))
                )
                .andExpect(status().isOk());

        // Get favorite offers
        result = mockMvc.perform(get(String.format("/api/users/%d/favorites/offers", loginResponseDTO2.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO2.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        List<OfferDTO> response = objectMapper.readValue(content, new TypeReference<>() {});

        assertNotNull(response);
        assertEquals(1, response.size());
        ServiceDTO offerDTO = (ServiceDTO) response.getFirst();
        assertNotNull(offerDTO);
        assertEquals(createServiceDTO.title(), offerDTO.getTitle());
        assertEquals(createServiceDTO.description(), offerDTO.getDescription());
        assertEquals(createServiceDTO.price(), offerDTO.getPrice());
        assertEquals(createServiceDTO.negotiable(), offerDTO.isNegotiable());

        // Remove favorite offer
        mockMvc.perform(patch(String.format("/api/users/%d/favorites/offers/remove", loginResponseDTO2.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO2.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RequestDTO(serviceDTO.getId())))
                )
                .andExpect(status().isOk());

        // Get favorite offers
        result = mockMvc.perform(get(String.format("/api/users/%d/favorites/offers", loginResponseDTO2.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO2.jwtToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        response = objectMapper.readValue(content, new TypeReference<>() {});

        assertNotNull(response);
        assertEquals(0, response.size());

        userRepository.deleteById(user1.id());
        userRepository.deleteById(user2.id());
        offerRepository.deleteById(serviceDTO.getId());
    }

}
