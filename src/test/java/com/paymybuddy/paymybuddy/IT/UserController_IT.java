package com.paymybuddy.paymybuddy.IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.paymybuddy.config.TestSecurityConfig;
import com.paymybuddy.paymybuddy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserController_IT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void addUser_successful() throws Exception {
        User user = User.builder()
                .userName("john")
                .email("john@mail.com")
                .password("password")
                .solde(BigDecimal.ZERO)
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("john"))
                .andExpect(jsonPath("$.email").value("john@mail.com"));
    }

    @Test
    public void addUser_duplicate_email() throws Exception {
        String email = "dup@mail.com";
        User user = User.builder()
                .userName("dup1")
                .email(email)
                .password("password")
                .solde(BigDecimal.ZERO)
                .build();


        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());


        User duplicateUser = User.builder()
                .userName("dup2")
                .email(email)
                .password("password2")
                .solde(BigDecimal.ZERO)
                .build();
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUser_missingField() throws Exception {
        User user = User.builder().userName("nobody").build();
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserById_found_boby() throws Exception {

        String uniqueMail = "boby@mail.com";
        User user = User.builder()
                .userName("boby")
                .email(uniqueMail)
                .password("password")
                .solde(BigDecimal.ZERO)
                .build();


        String response = mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();


        User saved = objectMapper.readValue(response, User.class);


        mockMvc.perform(get("/api/users/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("boby"))
                .andExpect(jsonPath("$.email").value(uniqueMail));
    }


    @Test
    public void getUserById_notFound() throws Exception {
        mockMvc.perform(get("/api/users/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_successful() throws Exception {
        User user = User.builder()
                .userName("mike")
                .email("mike@mail.com")
                .password("password")
                .solde(BigDecimal.valueOf(10))
                .build();
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andReturn().getResponse().getContentAsString();
        User saved = objectMapper.readValue(response, User.class);


        saved.setUserName("michael");
        saved.setEmail("michael@mail.com");

        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("michael"))
                .andExpect(jsonPath("$.email").value("michael@mail.com"));
    }

    @Test
    public void updateUser_notFound() throws Exception {
        User user = User.builder()
                .id(12222222) // id inexistant
                .userName("ghost")
                .email("ghost@mail.com")
                .password("pass")
                .solde(BigDecimal.ZERO)
                .build();
        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUser_successful() throws Exception {
        User user = User.builder()
                .userName("alice2")
                .email("alice2@mail.com")
                .password("password")
                .solde(BigDecimal.ZERO)
                .build();
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andReturn().getResponse().getContentAsString();
        User saved = objectMapper.readValue(response, User.class);
        mockMvc.perform(delete("/api/users/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(saved.getId())));
    }

    @Test
    public void deleteUser_notFound() throws Exception {
        mockMvc.perform(delete("/api/users/999999"))
                .andExpect(status().isBadRequest());
    }
}
