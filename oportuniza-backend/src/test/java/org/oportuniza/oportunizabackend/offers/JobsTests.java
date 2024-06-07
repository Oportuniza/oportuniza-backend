package org.oportuniza.oportunizabackend.offers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.oportuniza.oportunizabackend.TestUtils;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.offers.dto.CreateJobDTO;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.oportuniza.oportunizabackend.offers.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JobsTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Test
    //@WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getAllJobsTest() throws Exception {

        //Create user
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

        MvcResult result = mockMvc.perform(get("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<JobDTO> contentObject = objectMapper.readValue(content, new TypeReference<>() {} );

        assertNotNull(contentObject);
        assertEquals(contentObject.getFirst().getTitle(), CreateJobDTO.title());
        assertEquals(contentObject.getFirst().getDescription(), CreateJobDTO.description());
        assertEquals(contentObject.getFirst().isNegotiable(), CreateJobDTO.negotiable());
        assertEquals(contentObject.getFirst().getSalary(), CreateJobDTO.salary());
        assertEquals(contentObject.getFirst().getLocalization(), CreateJobDTO.localization());
        assertEquals(contentObject.getFirst().getWorkingModel(), CreateJobDTO.workingModel());
        assertEquals(contentObject.getFirst().getWorkingRegime(), CreateJobDTO.workingRegime());

    }

    @Test
    //@WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getUserJobs() throws Exception {
        // Create user
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

        MvcResult result = mockMvc.perform(get("/api/jobs/users/" + user1.id())
                    .header("Authorization", "Bearer " + loginResponseDTO.jwtToken()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<JobDTO> contentObject = objectMapper.readValue(content, new TypeReference<>() {} );

        assertNotNull(contentObject);
        assertEquals(contentObject.getFirst().getTitle(), CreateJobDTO.title());
        assertEquals(contentObject.getFirst().getDescription(), CreateJobDTO.description());
        assertEquals(contentObject.getFirst().isNegotiable(), CreateJobDTO.negotiable());
        assertEquals(contentObject.getFirst().getSalary(), CreateJobDTO.salary());
        assertEquals(contentObject.getFirst().getLocalization(), CreateJobDTO.localization());
        assertEquals(contentObject.getFirst().getWorkingModel(), CreateJobDTO.workingModel());
        assertEquals(contentObject.getFirst().getWorkingRegime(), CreateJobDTO.workingRegime());


    }

    @Test
    //@WithMockUser(username = "admin", roles = {"ADMIN"})
    public void createJobTest() throws Exception {
        // Create user
        var registerDTO = new RegisterDTO("joao@gmail.com", "123456", "123456789", "Joao da Silva");
        var user1 = testUtils.registerUser(registerDTO);
        // Login user
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        // Create Job
        var CreateJobDTO = new CreateJobDTO("Job Title", "Job Description", true, 1000.0, "Braga", "Remote", "Full-Time");

        MvcResult result = mockMvc.perform(post("/api/jobs/users/" + user1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken())
                        .content(objectMapper.writeValueAsString(CreateJobDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JobDTO job = objectMapper.readValue(content, JobDTO.class);

        assertNotNull(job);
        assertEquals(job.getTitle(), CreateJobDTO.title());
        assertEquals(job.getDescription(), CreateJobDTO.description());
        assertEquals(job.isNegotiable(), CreateJobDTO.negotiable());
        assertEquals(job.getSalary(), CreateJobDTO.salary());
        assertEquals(job.getLocalization(), CreateJobDTO.localization());
        assertEquals(job.getWorkingModel(), CreateJobDTO.workingModel());
        assertEquals(job.getWorkingRegime(), CreateJobDTO.workingRegime());

    }

    @Test
    //@WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getJobByIdTest() throws Exception {
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

        var jobId = objectMapper.readValue(jobResult.getResponse().getContentAsString(), JobDTO.class).getId();
        MvcResult result = mockMvc.perform(get("/api/jobs/" + jobId)
                .header("Authorization", "Bearer " + loginResponseDTO.jwtToken()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        JobDTO job = objectMapper.readValue(content, JobDTO.class);
        assertNotNull(job);
        assertEquals(job.getTitle(), CreateJobDTO.title());
        assertEquals(job.getDescription(), CreateJobDTO.description());
        assertEquals(job.isNegotiable(), CreateJobDTO.negotiable());
        assertEquals(job.getSalary(), CreateJobDTO.salary());
        assertEquals(job.getLocalization(), CreateJobDTO.localization());
        assertEquals(job.getWorkingModel(), CreateJobDTO.workingModel());
        assertEquals(job.getWorkingRegime(), CreateJobDTO.workingRegime());

        //Update Job
        var updateJobDTO = new JobDTO(jobId, "Updated Job Title", "Updated Job Description", true, 1000.0, "Braga", "Remote", "Full-Time");
        MvcResult updateResult = mockMvc.perform(put("/api/jobs/" + jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponseDTO.jwtToken())
                        .content(objectMapper.writeValueAsString(updateJobDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String updateContent = updateResult.getResponse().getContentAsString();

        JobDTO updatedJob = objectMapper.readValue(updateContent, JobDTO.class);
        assertNotNull(updatedJob);
        assertEquals(updatedJob.getTitle(), updateJobDTO.getTitle());
        assertEquals(updatedJob.getDescription(), updateJobDTO.getDescription());
        assertEquals(updatedJob.isNegotiable(), updateJobDTO.isNegotiable());
        assertEquals(updatedJob.getSalary(), updateJobDTO.getSalary());
        assertEquals(updatedJob.getLocalization(), updateJobDTO.getLocalization());
        assertEquals(updatedJob.getWorkingModel(), updateJobDTO.getWorkingModel());
        assertEquals(updatedJob.getWorkingRegime(), updateJobDTO.getWorkingRegime());

        //Delete Job

        MvcResult deleteResult = mockMvc.perform(delete("/api/jobs/" + jobId)
                .header("Authorization", "Bearer " + loginResponseDTO.jwtToken()))
                .andExpect(status().isOk())
                .andReturn();

        String deleteContent = deleteResult.getResponse().getContentAsString();

        assertEquals(deleteContent, "Job deleted successfully.");
    }


}