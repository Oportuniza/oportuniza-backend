package org.oportuniza.oportunizabackend.offers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.oportuniza.oportunizabackend.TestUtils;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.offers.dto.CreateServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.repository.ServiceRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class ServicesTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;
    @Autowired
    private ServiceRepository serviceRepository;
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
    public void getAllServicesTest() throws Exception {

        // Create user
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "123456789", "Joao da Silva");
        var user1 = testUtils.registerUser(registerDTO);
        // Login user
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        // Create Service
        var createServiceDTO = new CreateServiceDTO("Service Title", "Service Description", true, 1000.0);

        MvcResult result2 = mockMvc.perform(post("/api/services/users/" + user1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken())
                        .content(objectMapper.writeValueAsString(createServiceDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult result = mockMvc.perform(get("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        PageImpl<ServiceDTO> contentObject = TestUtils.deserializePage(content, ServiceDTO.class, objectMapper);

        assertNotNull(contentObject);
        assertEquals(contentObject.getTotalElements(), 1);
        var service = contentObject.getContent().getFirst();

        assertEquals(service.getPrice(), createServiceDTO.price());
        assertEquals(service.getDescription(), createServiceDTO.description());
        assertEquals(service.getTitle(), createServiceDTO.title());
        assertEquals(service.isNegotiable(), createServiceDTO.negotiable());

        serviceRepository.deleteById(service.getId());
        userRepository.deleteById(user1.id());
    }

    @Test
    public void getUserServices() throws Exception {
        // Create user
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "123456789", "Joao da Silva");
        var user1 = testUtils.registerUser(registerDTO);
        // Login user
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        // Create Service
        var createServiceDTO = new CreateServiceDTO("Service Title", "Service Description", true, 1000.0);

        MvcResult result2 = mockMvc.perform(post("/api/services/users/" + user1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken())
                        .content(objectMapper.writeValueAsString(createServiceDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult result = mockMvc.perform(get("/api/services/users/" + user1.id())
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        PageImpl<ServiceDTO> contentObject = TestUtils.deserializePage(content, ServiceDTO.class, objectMapper);

        assertNotNull(contentObject);
        assertEquals(contentObject.getTotalElements(), 1);
        var service = contentObject.getContent().getFirst();

        assertEquals(service.getPrice(), createServiceDTO.price());
        assertEquals(service.getDescription(), createServiceDTO.description());
        assertEquals(service.getTitle(), createServiceDTO.title());
        assertEquals(service.isNegotiable(), createServiceDTO.negotiable());

        serviceRepository.deleteById(service.getId());
        userRepository.deleteById(user1.id());
    }

    @Test
    public void createServiceTest() throws Exception {
        // Create user
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "123456789", "Joao da Silva");
        var user1 = testUtils.registerUser(registerDTO);
        // Login user
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        // Create Service
        var createServiceDTO = new CreateServiceDTO("Service Title", "Service Description", true, 1000.0);

        MvcResult result = mockMvc.perform(post("/api/services/users/" + user1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken())
                        .content(objectMapper.writeValueAsString(createServiceDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ServiceDTO service = objectMapper.readValue(content, ServiceDTO.class);

        assertNotNull(service);
        assertEquals(service.getTitle(), createServiceDTO.title());
        assertEquals(service.getDescription(), createServiceDTO.description());
        assertEquals(service.isNegotiable(), createServiceDTO.negotiable());
        assertEquals(service.getPrice(), createServiceDTO.price());

        serviceRepository.deleteById(service.getId());
        userRepository.deleteById(user1.id());
    }

    @Test
    public void getServiceByIdTest() throws Exception {
        //Create User
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "123456789", "Joao da Silva");
        var user1 = testUtils.registerUser(registerDTO);
        // Login user
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        // Create Service
        var createServiceDTO = new CreateServiceDTO("Service Title", "Service Description", true, 1000.0);
        MvcResult serviceResult = mockMvc.perform(post("/api/services/users/" + user1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken())
                        .content(objectMapper.writeValueAsString(createServiceDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        var serviceId = objectMapper.readValue(serviceResult.getResponse().getContentAsString(), ServiceDTO.class).getId();
        MvcResult result = mockMvc.perform(get("/api/services/" + serviceId)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ServiceDTO service = objectMapper.readValue(content, ServiceDTO.class);
        assertNotNull(service);
        assertEquals(service.getTitle(), createServiceDTO.title());
        assertEquals(service.getDescription(), createServiceDTO.description());
        assertEquals(service.isNegotiable(), createServiceDTO.negotiable());
        assertEquals(service.getPrice(), createServiceDTO.price());

        //Update Service
        var updateServiceDTO = new ServiceDTO(serviceId, "Updated Service Title", "Updated Service Description", true, 1000.0);
        MvcResult updateResult = mockMvc.perform(put("/api/services/" + serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken())
                        .content(objectMapper.writeValueAsString(updateServiceDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String updateContent = updateResult.getResponse().getContentAsString();

        ServiceDTO updatedService = objectMapper.readValue(updateContent, ServiceDTO.class);
        assertNotNull(updatedService);
        assertEquals(updatedService.getTitle(), updateServiceDTO.getTitle());
        assertEquals(updatedService.getDescription(), updateServiceDTO.getDescription());
        assertEquals(updatedService.isNegotiable(), updateServiceDTO.isNegotiable());
        assertEquals(updatedService.getPrice(), updateServiceDTO.getPrice());

        //Delete Service

        MvcResult deleteResult = mockMvc.perform(delete("/api/services/" + serviceId)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken()))
                .andExpect(status().isOk())
                .andReturn();

        String deleteContent = deleteResult.getResponse().getContentAsString();

        assertEquals(deleteContent, "Service deleted successfully.");

        userRepository.deleteById(user1.id());
    }
}