package org.oportuniza.oportunizabackend.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.oportuniza.oportunizabackend.TestUtils;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.offers.dto.CreateServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.repository.OfferRepository;
import org.oportuniza.oportunizabackend.users.dto.*;
import org.oportuniza.oportunizabackend.users.repository.ReviewRepository;
import org.oportuniza.oportunizabackend.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
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
    @Autowired
    private ReviewRepository reviewRepository;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.1")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    public void getUserTest() throws Exception {
        // Create user
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "local", "123456789", "Joao da Silva", null);
        var user1 = testUtils.registerUser(registerDTO);
        // Login user
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));
        // Get user
        MvcResult result = mockMvc.perform(get(String.format("/api/users/%d", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
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
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "local", "123456789", "Joao da Silva", null);
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
        var user1 = testUtils.registerUser(new RegisterDTO("joao@gmail.com", "123456", "local","123456789", "Joao da Silva", null));
        var user2 = testUtils.registerUser(new RegisterDTO("jose@gmail.com", "123456", "local","123456789", "Jose da Silva", null));
        var user3 = testUtils.registerUser(new RegisterDTO("joana@gmail.com", "123456", "local","123456789", "Joana da Silva", null));

        // Login User 1
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        // Add favorite users
        mockMvc.perform(patch(String.format("/api/users/%d/favorites/add", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(new RequestDTO(user2.id()))))
                .andExpect(status().isOk());

        mockMvc.perform(patch(String.format("/api/users/%d/favorites/add", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(new RequestDTO(user3.id()))))
                .andExpect(status().isOk());

        // Get favorite users
        MvcResult result = mockMvc.perform(get(String.format("/api/users/%d/favorites", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        PageImpl<UserDTO> response = TestUtils.deserializePage(content, UserDTO.class, objectMapper);

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
        var users = response.getContent();
        for (UserDTO user : users) {
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
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(new RequestDTO(user2.id()))))
                .andExpect(status().isOk());

        // Get favorite users
        result = mockMvc.perform(get(String.format("/api/users/%d/favorites", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        response = TestUtils.deserializePage(content, UserDTO.class, objectMapper);

        assertNotNull(response);
        assertEquals(response.getTotalElements(), 1);
        users = response.getContent();
        for (UserDTO user : users) {
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
        var user1 = testUtils.registerUser(new RegisterDTO("joao@gmail.com", "123456", "local","123456789", "Joao da Silva", null));
        var user2 = testUtils.registerUser(new RegisterDTO("jose@gmail.com", "123456", "local", "123456789", "Jose da Silva", null));

        // Login User 1
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        // Create Offer
        var createServiceDTO = new CreateServiceDTO("Limpador de Carros",
                "Limpo carros de luxo por dentro e por fora!",
                false, 200);

        MvcResult result = mockMvc.perform(post(String.format("/api/services/users/%d", loginResponseDTO.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
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
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(new RequestDTO(serviceDTO.getId())))
                )
                .andExpect(status().isOk());

        // Get favorite offers
        result = mockMvc.perform(get(String.format("/api/users/%d/favorites/offers", loginResponseDTO2.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO2.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn();

        // Remove favorite offer
        mockMvc.perform(patch(String.format("/api/users/%d/favorites/offers/remove", loginResponseDTO2.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO2.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(new RequestDTO(serviceDTO.getId())))
                )
                .andExpect(status().isOk());

        // Get favorite offers
        result = mockMvc.perform(get(String.format("/api/users/%d/favorites/offers", loginResponseDTO2.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO2.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();

        offerRepository.deleteById(serviceDTO.getId());
        userRepository.deleteById(user1.id());
        userRepository.deleteById(user2.id());
    }

    @Test
    public void reviewsTest() throws Exception {
        // Create user
        var user1 = testUtils.registerUser(new RegisterDTO("joao@gmail.com", "123456", "local", "123456789", "Joao da Silva", null));
        var user2 = testUtils.registerUser(new RegisterDTO("jose@gmail.com", "123456", "local", "123456789", "Jose da Silva", null));

        // Login user 1
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        // Create review - user 1 to user 2
        CreateReviewDTO createReviewDTO = new CreateReviewDTO(user1.id(), user2.id(), 3);
        MvcResult result = mockMvc.perform(post("/api/reviews")
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(createReviewDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ReviewDTO reviewDTO = objectMapper.readValue(content, ReviewDTO.class);

        assertNotNull(reviewDTO);
        assertEquals(createReviewDTO.reviewerId(), reviewDTO.reviewerId());
        assertEquals(createReviewDTO.reviewedId(), reviewDTO.reviewedId());
        assertEquals(createReviewDTO.rating(), reviewDTO.rating());

        // Get user 2
        result = mockMvc.perform(get(String.format("/api/users/%d", user2.id()))
                        .header("Authorization", String.format("Bearer %s", loginResponseDTO.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        UserDTO user2DTO = objectMapper.readValue(content, UserDTO.class);

        assertNotNull(user2DTO);
        assertEquals(3, user2DTO.rating());
        assertEquals(1, user2DTO.reviewsCount());

        reviewRepository.deleteById(reviewDTO.id());
        userRepository.deleteById(user1.id());
        userRepository.deleteById(user2.id());
    }

}
