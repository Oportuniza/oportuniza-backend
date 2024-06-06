package org.oportuniza.oportunizabackend.offers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.oportuniza.oportunizabackend.TestUtils;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.offers.dto.CreateServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ServicesTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Test
    //@WithMockUser(username = "admin", roles = {"ADMIN"})
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
        List<ServiceDTO> contentObject = objectMapper.readValue(content, new TypeReference<>() {} );

        assertNotNull(contentObject);
        assertEquals(contentObject.getFirst().getPrice(), createServiceDTO.price());
        assertEquals(contentObject.getFirst().getDescription(), createServiceDTO.description());
        assertEquals(contentObject.getFirst().getTitle(), createServiceDTO.title());
        assertEquals(contentObject.getFirst().isNegotiable(), createServiceDTO.negotiable());
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
        List<ServiceDTO> contentObject = objectMapper.readValue(content, new TypeReference<>() {} );

        assertNotNull(contentObject);
        assertEquals(contentObject.getFirst().getPrice(), createServiceDTO.price());
        assertEquals(contentObject.getFirst().getDescription(), createServiceDTO.description());
        assertEquals(contentObject.getFirst().getTitle(), createServiceDTO.title());
        assertEquals(contentObject.getFirst().isNegotiable(), createServiceDTO.negotiable());
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
    }
}