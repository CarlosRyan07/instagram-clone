package br.edu.ifpb.instagram.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ifpb.instagram.model.request.LoginRequest;
import br.edu.ifpb.instagram.model.request.UserDetailsRequest;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;

    @Test
    void testSignInCorrectly() throws Exception {
        
        UserDetailsRequest user = new UserDetailsRequest(null, "gustavo@123", "1234", "Gustavo Ferreira", "gudalol");
        mockMvc.perform(post("auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(user)));
    
        LoginRequest loginRequest = new LoginRequest("Gudalol", "1234");
        
        mockMvc.perform(post("/auth/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk());

        
    }

}
