package org.oportuniza.oportunizabackend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.oportuniza.oportunizabackend.authentication.dto.LoginDTO;
import org.oportuniza.oportunizabackend.authentication.dto.LoginResponseDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterResponseDTO;
import org.oportuniza.oportunizabackend.offers.dto.CreateJobDTO;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        // Capture and parse the register response
        String registerResponseContent = registerResult.getResponse().getContentAsString();
        return objectMapper.readValue(registerResponseContent, RegisterResponseDTO.class);
    }

    public LoginResponseDTO loginUser(LoginDTO loginDTO) throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        // Capture and parse the login response
        String loginResponseContent = loginResult.getResponse().getContentAsString();
        return objectMapper.readValue(loginResponseContent, LoginResponseDTO.class);
    }

    public static <T> PageImpl<T> deserializePage(String content, Class<T> contentClass, ObjectMapper objectMapper) throws Exception {
        JsonNode rootNode = objectMapper.readTree(content);
        JsonNode contentNode = rootNode.get("content");
        JsonNode totalElementsNode = rootNode.get("totalElements");

        List<T> contentList = objectMapper.readValue(
                contentNode.traverse(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, contentClass)
        );

        long totalElements = totalElementsNode.asLong();

        Pageable pageable = PageRequest.of(0, totalElements > 0 ? contentList.size() : 1);  // PageRequest can be adjusted based on your actual needs

        return new PageImpl<>(contentList, pageable, totalElements);
    }
}
