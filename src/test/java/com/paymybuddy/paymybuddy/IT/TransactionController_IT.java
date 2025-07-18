package com.paymybuddy.paymybuddy.IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.paymybuddy.config.TestSecurityConfig;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TransactionController_IT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void getTransactionById_found() throws Exception {
        mockMvc.perform(get("/api/transactions/{id}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.description").value("Remboursement dîner"))
                .andExpect(jsonPath("$.amount").value(42.50))
                .andExpect(jsonPath("$.fromUser.id").value(2))
                .andExpect(jsonPath("$.toUser.id").value(3));
    }


    @Test
    public void getTransactionById_notFound() throws Exception {
        mockMvc.perform(get("/api/transactions/{id}", 999))
                .andExpect(status().isNotFound());
    }


    @Test
    public void getAllTransactionsByUserId_alice() throws Exception {
        mockMvc.perform(get("/api/transactions/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)) // alice n'est sender que sur l'id=1
                .andExpect(jsonPath("$[0].fromUser.id").value(1));
    }


    @Test
    public void getAllTransactionsByUserId_carol() throws Exception {
        mockMvc.perform(get("/api/transactions/users/{userId}", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)) // carol envoie 2 transactions
                .andExpect(jsonPath("$[0].fromUser.id").value(3));
    }


    @Test
    public void getAllTransactionsByUserId_unknown() throws Exception {
        mockMvc.perform(get("/api/transactions/users/{userId}", 42))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    public void addTransaction_successful() throws Exception {
        String txJson = """
                {
                    "description": "Nouvelle transaction test",
                    "amount": 15.00,
                    "fromUser": { "id": 2 },
                    "toUser":   { "id": 1 }
                }""";

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(txJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fromUser.id").value(2))
                .andExpect(jsonPath("$.toUser.id").value(1))
                .andExpect(jsonPath("$.amount").value(15.00))
                .andExpect(jsonPath("$.description").value("Nouvelle transaction test"));
    }


    @Test
    public void addTransaction_invalidUser() throws Exception {
        String txJson = """
                {
                    "description": "fail",
                    "amount": 15.00,
                    "fromUser": { "id": 999 },
                    "toUser":   { "id": 1 }
                }""";

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(txJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void addTransaction_negativeAmount() throws Exception {
        String txJson = """
                {
                    "description": "Negative fail",
                    "amount": -10.00,
                    "fromUser": { "id": 3 },
                    "toUser":   { "id": 2 }
                }""";

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(txJson))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void addTransaction_insufficientFunds() throws Exception {
        String txJson = """
            {
                "description": "Déficit interdit",
                "amount": 150.00,
                "fromUser": { "id": 2 },
                "toUser":   { "id": 1 }
            }""";

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(txJson))
                .andExpect(status().isBadRequest());
    }
}
