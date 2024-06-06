package org.oportuniza.oportunizabackend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.LoginResponseDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterResponseDTO;
import org.oportuniza.oportunizabackend.offers.dto.CreateJobDTO;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class TestUtils {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public RegisterResponseDTO registerUser(RegisterDTO registerDTO) throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        // Capture and parse the register response
        String registerResponseContent = registerResult.getResponse().getContentAsString();
        return objectMapper.readValue(registerResponseContent, RegisterResponseDTO.class);
    }

    public LoginResponseDTO loginUser(LoginDTO loginDTO) throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        // Capture and parse the login response
        String loginResponseContent = loginResult.getResponse().getContentAsString();
        return objectMapper.readValue(loginResponseContent, LoginResponseDTO.class);
    }

    public JobDTO createJob(CreateJobDTO createJobDTO) throws Exception {
        MvcResult jobResult = mockMvc.perform(post("/api/jobs/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createJobDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String jobResponseContent = jobResult.getResponse().getContentAsString();
        return objectMapper.readValue(jobResponseContent, JobDTO.class);
    }
}
