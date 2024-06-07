package org.oportuniza.oportunizabackend.applications;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.oportuniza.oportunizabackend.TestUtils;
import org.oportuniza.oportunizabackend.applications.model.Application;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.offers.dto.CreateJobDTO;
import org.oportuniza.oportunizabackend.applications.dto.CreateApplicationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationsTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Test
    public void testGetApplicationsByUserId() throws Exception {
        //Create User
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "123456789", "Joao da Silva");
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

        //Create User 2
        var registerDTO2 = new RegisterDTO("gui@gmail.com", "123456", "123456789", "Gui da Silva");
        var user2 = testUtils.registerUser(registerDTO2);

        // Login user 2
        var loginResponseDTO2 = testUtils.loginUser(new LoginDTO("gui@gmail.com", "123456"));

        CreateApplicationDTO createApplicationDTO = new CreateApplicationDTO("Gui", "Silva", "gui@gmail.com", "I am the best", "www.resume.com", List.of("www.document.com"));

        String createApplicationDTOJson = objectMapper.writeValueAsString(createApplicationDTO);

        var idJob = testUtils.getJobId(jobResult);

        System.out.println(idJob);

        // Create Application
        MvcResult applicationResult = mockMvc.perform(post("/api/applications/users/" + user2.id() + "/offers/" + idJob)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO2.jwtToken())
                        .content(createApplicationDTOJson))
                .andExpect(status().isCreated())
                .andReturn();

        // Get applications by User Id
        MvcResult applicationsByUserId = mockMvc.perform(get("/api/applications/user/" + user2.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO2.jwtToken())) // Use the token of user2
                .andExpect(status().isOk()).andReturn();

        // Get the content of the applicationResult and applicationsByUserId
        String applicationResultContent = applicationResult.getResponse().getContentAsString();
        String applicationsByUserIdContent = applicationsByUserId.getResponse().getContentAsString();

        // Convert the content to Application and List<Application> respectively
        Application newCreatedApplication = objectMapper.readValue(applicationResultContent, Application.class);
        List<Application> applicationsByUser = objectMapper.readValue(applicationsByUserIdContent, new TypeReference<>() {});

        // Find the application in the list of applications by user that matches the created application
        Application foundApplication = applicationsByUser.stream()
                .filter(application -> application.getId().equals(newCreatedApplication.getId()))
                .findFirst()
                .orElse(null);

        // Assert that the found application is not null and its fields match the created application
        assertNotNull(foundApplication);
        assertEquals(newCreatedApplication.getId(), foundApplication.getId());
        assertEquals(newCreatedApplication.getFirstName(), foundApplication.getFirstName());
        assertEquals(newCreatedApplication.getLastName(), foundApplication.getLastName());
        assertEquals(newCreatedApplication.getEmail(), foundApplication.getEmail());
        assertEquals(newCreatedApplication.getMessage(), foundApplication.getMessage());
        assertEquals(newCreatedApplication.getResumeUrl(), foundApplication.getResumeUrl());
        assertEquals(newCreatedApplication.getDocuments(), foundApplication.getDocuments());
    }



}
