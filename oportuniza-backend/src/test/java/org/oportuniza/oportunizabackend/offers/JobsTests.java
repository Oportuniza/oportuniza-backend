package org.oportuniza.oportunizabackend.offers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.oportuniza.oportunizabackend.TestUtils;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.offers.dto.CreateJobDTO;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.oportuniza.oportunizabackend.offers.model.Job;
import org.oportuniza.oportunizabackend.offers.repository.JobRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class JobsTests {

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

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
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
        PageImpl<JobDTO> contentObject = TestUtils.deserializePage(content, JobDTO.class, objectMapper);

        assertNotNull(contentObject);
        assertEquals(contentObject.getTotalElements(), 1);
        var job = contentObject.getContent().getFirst();
        assertEquals(job.getTitle(), CreateJobDTO.title());
        assertEquals(job.getDescription(), CreateJobDTO.description());
        assertEquals(job.isNegotiable(), CreateJobDTO.negotiable());
        assertEquals(job.getSalary(), CreateJobDTO.salary());
        assertEquals(job.getLocalization(), CreateJobDTO.localization());
        assertEquals(job.getWorkingModel(), CreateJobDTO.workingModel());
        assertEquals(job.getWorkingRegime(), CreateJobDTO.workingRegime());

        jobRepository.deleteById(job.getId());
        userRepository.deleteById(user1.id());
    }

    @Test
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
        PageImpl<JobDTO> contentObject = TestUtils.deserializePage(content, JobDTO.class, objectMapper);

        assertNotNull(contentObject);
        assertEquals(contentObject.getTotalElements(), 1);
        var job = contentObject.getContent().getFirst();
        assertEquals(job.getDescription(), CreateJobDTO.description());
        assertEquals(job.getTitle(), CreateJobDTO.title());
        assertEquals(job.isNegotiable(), CreateJobDTO.negotiable());
        assertEquals(job.getSalary(), CreateJobDTO.salary());
        assertEquals(job.getLocalization(), CreateJobDTO.localization());
        assertEquals(job.getWorkingModel(), CreateJobDTO.workingModel());
        assertEquals(job.getWorkingRegime(), CreateJobDTO.workingRegime());

        jobRepository.deleteById(job.getId());
        userRepository.deleteById(user1.id());
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

        jobRepository.deleteById(job.getId());
        userRepository.deleteById(user1.id());
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

        userRepository.deleteById(user1.id());
    }

    @Test
    public void testFilters() throws Exception {
        List<Job> jobs = new ArrayList<>();
        var job1 = new Job();
        job1.setTitle("Job 1");
        job1.setDescription("Description 1");
        job1.setNegotiable(true);
        job1.setSalary(1000.0);
        job1.setLocalization("Braga");
        job1.setWorkingModel("Remote");
        job1.setWorkingRegime("Full-Time");
        jobs.add(job1);

        var job2 = new Job();
        job2.setTitle("Job 2");
        job2.setDescription("Description 2");
        job2.setNegotiable(false);
        job2.setSalary(2000.0);
        job2.setLocalization("Porto");
        job2.setWorkingModel("Presential");
        job2.setWorkingRegime("Part-Time");
        jobs.add(job2);

        var job3 = new Job();
        job3.setTitle("Job 3");
        job3.setDescription("Description 3");
        job3.setNegotiable(true);
        job3.setSalary(3000.0);
        job3.setLocalization("Lisboa");
        job3.setWorkingModel("Remote");
        job3.setWorkingRegime("Full-Time");
        jobs.add(job3);

        jobRepository.saveAll(jobs);

        //Create User
        var user1 = testUtils.registerUser(new RegisterDTO("joao@gmail.com", "123456", "123456789", "Joao da Silva"));
        // Login user
        var loginResponseDTO = testUtils.loginUser(new LoginDTO("joao@gmail.com", "123456"));

        MvcResult result = mockMvc.perform(get("/api/jobs?minSalary=1000&maxSalary=2000&workingModel=Remote&workingRegime=Full-Time&negotiable=true")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + loginResponseDTO.jwtToken()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        PageImpl<JobDTO> contentObject = TestUtils.deserializePage(content, JobDTO.class, objectMapper);

        assertNotNull(contentObject);
        assertEquals(contentObject.getTotalElements(), 1);

        jobRepository.deleteAll(jobs);
    }


}
