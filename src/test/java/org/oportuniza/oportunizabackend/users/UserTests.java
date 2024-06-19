package org.oportuniza.oportunizabackend.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.oportuniza.oportunizabackend.authentication.dto.LoginResponseDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.offers.dto.CreateServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.users.dto.RequestDTO;
import org.oportuniza.oportunizabackend.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
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
    private UserRepository userRepository;

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
    public void favoriteOffersTest() throws Exception {
        // Register user 1
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "local", "123456789", "Joao", null, "Vila Nova de Famalicão", "Braga");
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseContent = registerResult.getResponse().getContentAsString();
        LoginResponseDTO user1 = objectMapper.readValue(registerResponseContent, LoginResponseDTO.class);

        // Create Offer
        var createServiceDTO = new CreateServiceDTO(
                "Limpador de Carros",
                "Limpo carros de luxo por dentro e por fora!",
                "Viana",
                "Ponte",
                true,
                100
        );

        MockMultipartFile serviceJson = new MockMultipartFile(
                "service",
                "user.json",
                "application/json",
                objectMapper.writeValueAsBytes(createServiceDTO)
        );

        MvcResult result = mockMvc.perform(multipart(String.format("/api/services/users/%d", user1.id()))
                        .file(serviceJson)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                        .header("Authorization", String.format("Bearer %s", user1.jwtToken()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ServiceDTO serviceDTO = objectMapper.readValue(content, ServiceDTO.class);

        // Add favorite offer
        mockMvc.perform(patch(String.format("/api/users/%d/favorites/offers/add", user1.id()))
                        .header("Authorization", String.format("Bearer %s", user1.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(new RequestDTO(serviceDTO.getId())))
                )
                .andExpect(status().isOk());

        // Get favorite offers
        result = mockMvc.perform(get(String.format("/api/users/%d/favorites/offers", user1.id()))
                        .header("Authorization", String.format("Bearer %s", user1.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        System.out.println(content);

        // Remove favorite offer
        mockMvc.perform(patch(String.format("/api/users/%d/favorites/offers/remove", user1.id()))
                        .header("Authorization", String.format("Bearer %s", user1.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(new RequestDTO(serviceDTO.getId())))
                )
                .andExpect(status().isOk());

        // Get favorite offers
        result = mockMvc.perform(get(String.format("/api/users/%d/favorites/offers", user1.id()))
                        .header("Authorization", String.format("Bearer %s", user1.jwtToken()))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        System.out.println(content);
    }

}
