package com.paymybuddy.paymybuddy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.paymybuddy.config.TestSecurityConfig;
import com.paymybuddy.paymybuddy.model.Connexion;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.service.ConnexionService;
import com.paymybuddy.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = ConnexionController.class)
public class ConnexionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConnexionService connexionServiceMocked;

    @MockitoBean
    private UserService userServiceMocked;

    @Autowired
    private ObjectMapper objectMapper;

    private Connexion con1, con2, con3;
    private User user1, user2;
    private List<Connexion> connexionsListe;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1)
                .userName("Alice")
                .email("alice@mail.com")
                .password("alicePwd")
                .solde(BigDecimal.valueOf(100))
                .build();

        user2 = User.builder()
                .id(2)
                .userName("Bob")
                .email("bob@mail.com")
                .password("bobPwd")
                .solde(BigDecimal.valueOf(50))
                .build();

        con1 = Connexion.builder()
                .fromUser(user1.getId())
                .toUser(user2.getId())
                .build();

        con2 = Connexion.builder()
                .fromUser(user2.getId())
                .toUser(3)
                .build();

        con3 = Connexion.builder()
                .fromUser(user1.getId())
                .toUser(3)
                .build();

        connexionsListe = Arrays.asList(con1, con2, con3);
    }


    // 3


    @Test
    public void getAllConnexionforAnUserID_list() throws Exception {
        when(connexionServiceMocked.getAllConnexionsbyUserId(1)).thenReturn(List.of(con1, con3));

        mockMvc.perform(get("/api/connexions/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fromUser").value(user1.getId()));
    }


    @Test
    public void getAllConnexionforAnUserID_empty() throws Exception {
        when(connexionServiceMocked.getAllConnexionsbyUserId(42)).thenReturn(List.of());

        mockMvc.perform(get("/api/connexions/{userId}", 42))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // 7
    @Test
    public void addConnexionByUsersID_success() throws Exception {
        when(userServiceMocked.getUserById(1)).thenReturn(Optional.of(user1));
        when(userServiceMocked.getUserById(2)).thenReturn(Optional.of(user2));
        when(connexionServiceMocked.addConnexion(con1)).thenReturn(Optional.of(con1));

        mockMvc.perform(post("/api/connexions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(con1)))  // <-- body sérialisé
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromUser").value(1))
                .andExpect(jsonPath("$.toUser").value(2));
    }

    // 8
    @Test
    public void addConnexion_notAdded() throws Exception {
        when(connexionServiceMocked.addConnexion(con1)).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/connexions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(con1)))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void deleteConnexion_success() throws Exception {
        when(connexionServiceMocked.deleteConnexion(ArgumentMatchers.any())).thenReturn(Optional.of(con1));

        mockMvc.perform(delete("/api/connexions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(con1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromUser").value(1))
                .andExpect(jsonPath("$.toUser").value(2));
    }


    @Test
    public void deleteConnexion_notFound() throws Exception {

        when(connexionServiceMocked.deleteConnexion(con1)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/connexions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(con1)))
                .andExpect(status().isBadRequest());
    }


}
