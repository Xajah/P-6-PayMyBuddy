package com.paymybuddy.paymybuddy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.paymybuddy.config.TestSecurityConfig;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userServiceMocked;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1)
                .userName("Luc")
                .email("luc@mail.com")
                .solde(new BigDecimal("60"))
                .password("yoputo")
                .connexions(new ArrayList<>())
                .receivedTransactionsRecues(new ArrayList<>())
                .sendedTransactions(new ArrayList<>())
                .build();
        user2 = User.builder()
                .id(2)
                .userName("Marc")
                .email("marco@mail.com")
                .solde(new BigDecimal("65"))
                .password("Yefre321")
                .connexions(new ArrayList<>())
                .receivedTransactionsRecues(new ArrayList<>())
                .sendedTransactions(new ArrayList<>())
                .build();
        user3 = User.builder()
                .id(3)
                .userName("lorelei")
                .email("moresque@mail.com")
                .solde(new BigDecimal("20"))
                .password("hgfrth321")
                .connexions(new ArrayList<>())
                .receivedTransactionsRecues(new ArrayList<>())
                .sendedTransactions(new ArrayList<>())
                .build();

        UserController controllerUnderTest = new UserController(userServiceMocked);
    }

    @Test
    public void getUserById_succes() throws Exception {
        when(userServiceMocked.getUserById(1)).thenReturn(Optional.of(user1));

        mockMvc.perform(get("/api/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(user1.getUserName()));

    }

    @Test
    public void getUserById_fail() throws Exception {
        when(userServiceMocked.getUserById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{userId}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addUser_succes() throws Exception {
        when(userServiceMocked.addUser(user1)).thenReturn(Optional.of(user1));

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(user1.getUserName()));
    }

    @Test
    public void addUser_fail() throws Exception {
        when(userServiceMocked.addUser(user1)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isBadRequest());


    }

    @Test
    public void updateUser_succes() throws Exception {
        when(userServiceMocked.updateUser(user1)).thenReturn(Optional.of(user1));

        mockMvc.perform(put("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(user1.getUserName()));
    }

    @Test
    public void updateUser_fail() throws Exception {
        when(userServiceMocked.updateUser(user1)).thenReturn(Optional.empty());

        mockMvc.perform((put("/api/users"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void deleteUser_succes() throws Exception {
        when(userServiceMocked.deleteUser(user1.getId())).thenReturn(true);

        mockMvc.perform(delete("/api/users/{userId}", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(user1.getId()))); // on vérifie que le body = userId
    }


    @Test
    public void deleteUser_fail() throws Exception {
        when(userServiceMocked.deleteUser(user1.getId())).thenReturn(false);

        mockMvc.perform(delete("/api/users/{userId}", user1.getId()))
                .andExpect(status().isBadRequest());
    }

}
