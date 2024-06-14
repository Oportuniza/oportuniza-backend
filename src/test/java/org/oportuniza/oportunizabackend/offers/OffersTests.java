package org.oportuniza.oportunizabackend.offers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.oportuniza.oportunizabackend.TestUtils;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.offers.dto.CreateJobDTO;
import org.oportuniza.oportunizabackend.offers.dto.CreateServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.repository.JobRepository;
import org.oportuniza.oportunizabackend.offers.repository.OfferRepository;
import org.oportuniza.oportunizabackend.offers.repository.ServiceRepository;
import org.oportuniza.oportunizabackend.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class OffersTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.1")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private OfferRepository offerRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    public void getAllOffersTest() throws Exception {
        //Create user
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "local", "123456789", "Joao da Silva", null);
        var user1 = testUtils.registerUser(registerDTO);
        // Login user
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        // Create Job
        var CreateJobDTO = new CreateJobDTO("Job Title", "Job Description", true, 1000.0, "Braga", "Remote", "Full-Time");
        MvcResult jobResult = mockMvc.perform(post("/api/jobs/users/" + user1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken())
                        .content(objectMapper.writeValueAsString(CreateJobDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        var jobId = objectMapper.readValue(jobResult.getResponse().getContentAsString(), JobDTO.class).getId();

        // Create Service
        var createServiceDTO = new CreateServiceDTO("Service Title", "Service Description", true, 1000.0);
        MvcResult serviceResult = mockMvc.perform(post("/api/services/users/" + user1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken())
                        .content(objectMapper.writeValueAsString(createServiceDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        var serviceId = objectMapper.readValue(serviceResult.getResponse().getContentAsString(), ServiceDTO.class).getId();

        // Get all offers
        MvcResult offersResult = mockMvc.perform(get("/api/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken()))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(offersResult.getResponse().getContentAsString());

        offerRepository.deleteById(serviceId);
        offerRepository.deleteById(jobId);
        userRepository.deleteById(user1.id());
    }
}
