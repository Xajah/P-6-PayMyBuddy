package com.paymybuddy.paymybuddy.IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.paymybuddy.config.TestSecurityConfig;
import com.paymybuddy.paymybuddy.model.Connexion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ConnexionController_IT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void getAllConnexionForUser1_ReturnsTwoConnexions() throws Exception {
        mockMvc.perform(get("/api/connexions/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fromUser").value(1))
                .andExpect(jsonPath("$[1].fromUser").value(1));
    }


    @Test
    public void getAllConnexionForUnknownUser_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/connexions/{userId}", 42))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    public void addConnexion_AddsConnexionToDatabase() throws Exception {
        Connexion newCon = Connexion.builder()
                .fromUser(2)
                .toUser(1)
                .build();

        mockMvc.perform(post("/api/connexions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newCon)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromUser").value(2))
                .andExpect(jsonPath("$.toUser").value(1));


        mockMvc.perform(get("/api/connexions/{userId}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }


    @Test
    public void addConnexion_AlreadyExists_ReturnsBadRequest() throws Exception {
        Connexion alreadyExists = Connexion.builder()
                .fromUser(1)
                .toUser(2)
                .build();

        mockMvc.perform(post("/api/connexions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(alreadyExists)))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void deleteConnexion_ExistingConnexion_ReturnsOk() throws Exception {
        Connexion toDelete = Connexion.builder()
                .fromUser(1)
                .toUser(2)
                .build();

        mockMvc.perform(delete("/api/connexions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(toDelete)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromUser").value(1))
                .andExpect(jsonPath("$.toUser").value(2));


        mockMvc.perform(get("/api/connexions/{userId}", 1))
                .andExpect(jsonPath("$.length()").value(1));
    }


    @Test
    public void deleteConnexion_NotFound_ReturnsBadRequest() throws Exception {
        Connexion notInDb = Connexion.builder()
                .fromUser(3)
                .toUser(42)
                .build();

        mockMvc.perform(delete("/api/connexions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(notInDb)))
                .andExpect(status().isBadRequest());
    }
}
