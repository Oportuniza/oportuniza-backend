package org.oportuniza.oportunizabackend.applications;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.oportuniza.oportunizabackend.TestUtils;
import org.oportuniza.oportunizabackend.applications.dto.ApplicationDTO;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.offers.dto.CreateJobDTO;
import org.oportuniza.oportunizabackend.applications.dto.CreateApplicationDTO;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationsTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

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
    public void testGetApplicationsByUserId() throws Exception {
        //Create User
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "local", "123456789", "Joao da Silva", null, "Vila Nova de Famalicão", "Braga");
        var registerDTO2 = new RegisterDTO("gui@gmail.com", "123456", "local", "123456789", "Gui da Silva", null, "Vila Nova de Famalicão", "Braga");
        var user1 = testUtils.registerUser(registerDTO);
        var user2 = testUtils.registerUser(registerDTO2);

        // Login user
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        // Create Job
        var CreateJobDTO = new CreateJobDTO("Job Title", "Job Description", true, 1000.0, "Braga", "Braga", "Remote", "Full-Time");

        MvcResult jobResult = mockMvc.perform(post("/api/jobs/users/" + user1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken())
                        .content(objectMapper.writeValueAsString(CreateJobDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String jobResponseContent = jobResult.getResponse().getContentAsString();
        JobDTO jobDTO = objectMapper.readValue(jobResponseContent, JobDTO.class);

        CreateApplicationDTO createApplicationDTO = new CreateApplicationDTO("Gui", "Silva", "gui@gmail.com", "I am the best");

        // Generate a simple image in the code
        // Generate multiple files
        BufferedImage image1 = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics1 = image1.createGraphics();
        graphics1.setColor(Color.GREEN);
        graphics1.fillRect(0, 0, image1.getWidth(), image1.getHeight());
        graphics1.dispose();

        BufferedImage image2 = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2 = image2.createGraphics();
        graphics2.setColor(Color.BLUE);
        graphics2.fillRect(0, 0, image2.getWidth(), image2.getHeight());
        graphics2.dispose();

        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ImageIO.write(image1, "jpg", baos1);
        byte[] imageBytes1 = baos1.toByteArray();

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        ImageIO.write(image2, "jpg", baos2);
        byte[] imageBytes2 = baos2.toByteArray();

        MockMultipartFile resumeFile1 = new MockMultipartFile("files", "resume1.jpg", "image/jpeg", imageBytes1);
        MockMultipartFile resumeFile2 = new MockMultipartFile("files", "resume2.jpg", "image/jpeg", imageBytes2);
        MockMultipartFile applicationPart = new MockMultipartFile("application", "application.json", "application/json", objectMapper.writeValueAsString(createApplicationDTO).getBytes());

        // Perform the multipart request
        MvcResult applicationResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/applications/users/" + user2.id() + "/offers/" + jobDTO.getId())
                        .file(resumeFile1)
                        .file(resumeFile2)
                        .file(applicationPart)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        }))
                .andExpect(status().isCreated())
                .andReturn();

        String applicationResultContent = applicationResult.getResponse().getContentAsString();
        ApplicationDTO newCreatedApplication = objectMapper.readValue(applicationResultContent, ApplicationDTO.class);

        assertNotNull(newCreatedApplication);
        assertEquals(newCreatedApplication.firstName(), createApplicationDTO.firstName());
        assertEquals(newCreatedApplication.lastName(), createApplicationDTO.lastName());
        assertEquals(newCreatedApplication.email(), createApplicationDTO.email());
        assertEquals(newCreatedApplication.message(), createApplicationDTO.message());

        // Get applications
        MvcResult applicationsByUserId = mockMvc.perform(get("/api/applications/user/" + user2.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken())) // Use the token of user2
                .andExpect(status().isOk())
                .andReturn();

        String applicationsByUserIdContent = applicationsByUserId.getResponse().getContentAsString();
        PageImpl<ApplicationDTO> applicationsByUser = TestUtils.deserializePage(applicationsByUserIdContent, ApplicationDTO.class, objectMapper);

        assertEquals(1, applicationsByUser.getTotalElements());
        var foundApplication = applicationsByUser.getContent().getFirst();

        // Assert that the found application is not null and its fields match the created application
        assertNotNull(foundApplication);
        assertEquals(newCreatedApplication.id(), foundApplication.id());
        assertEquals(newCreatedApplication.firstName(), foundApplication.firstName());
        assertEquals(newCreatedApplication.lastName(), foundApplication.lastName());
        assertEquals(newCreatedApplication.email(), foundApplication.email());
        assertEquals(newCreatedApplication.message(), foundApplication.message());
        assertEquals(newCreatedApplication.resumeUrl(), foundApplication.resumeUrl());
        assertEquals(newCreatedApplication.documentsUrls(), foundApplication.documentsUrls());
    }



}
