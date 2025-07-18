package com.paymybuddy.paymybuddy.controller;

import com.paymybuddy.paymybuddy.config.TestSecurityConfig;
import com.paymybuddy.paymybuddy.model.Transaction;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionServiceMocked;

    private User userA;
    private User userB;
    private User userC;
    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;
    private List<Transaction> transactions;
    private List<Transaction> userATransactions;

    @BeforeEach
    void setUp() {
        userA = User.builder()
                .id(1).userName("alice").email("alice@mail.com").password("alicepwd")
                .solde(new BigDecimal("100.00"))
                .connexions(new ArrayList<>())
                .sendedTransactions(new ArrayList<>())
                .receivedTransactionsRecues(new ArrayList<>())
                .build();
        userB = User.builder()
                .id(2).userName("bob").email("bob@mail.com").password("bobpwd")
                .solde(new BigDecimal("50.00"))
                .connexions(new ArrayList<>())
                .sendedTransactions(new ArrayList<>())
                .receivedTransactionsRecues(new ArrayList<>())
                .build();
        userC = User.builder()
                .id(3).userName("Alain").email("Alain@mail.com").password("Alain546")
                .solde(new BigDecimal("80.00")).connexions(new ArrayList<>())
                .sendedTransactions(new ArrayList<>())
                .receivedTransactionsRecues(new ArrayList<>()).build();

        transaction1 = Transaction.builder()
                .id(1)
                .amount(new BigDecimal(10))
                .fromUser(userA)
                .toUser(userB)
                .description("Café").build();
        transaction2 = Transaction.builder()
                .id(2)
                .amount(new BigDecimal("38"))
                .fromUser(userA)
                .toUser(userC)
                .description("Avance Course")
                .build();
        transaction3 = Transaction.builder()
                .id(3)
                .amount(new BigDecimal("25"))
                .fromUser(userB)
                .toUser(userC)
                .description("Partage Essence")
                .build();
        transactions = Arrays.asList(transaction1, transaction2, transaction3);
        userATransactions = Arrays.asList(transaction1, transaction2);
    }


    @Test
    public void getTransactionById_found() throws Exception {
        when(transactionServiceMocked.getTransactionById(1)).thenReturn(Optional.of(transaction1));

        mockMvc.perform(get("/api/transactions/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transaction1.getId()))
                .andExpect(jsonPath("$.fromUser.id").value(userA.getId()))
                .andExpect(jsonPath("$.toUser.id").value(userB.getId()));
    }


    @Test
    public void getTransactionById_notFound() throws Exception {
        when(transactionServiceMocked.getTransactionById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/transactions/{id}", 1))
                .andExpect(status().isNotFound());
    }


    @Test
    public void getAllTransactionsByAnUserId_found() throws Exception {
        when(transactionServiceMocked.getTransactionsByUserId(1)).thenReturn(userATransactions);

        mockMvc.perform(get("/api/transactions/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(userATransactions.size()))
                .andExpect(jsonPath("$[0].fromUser.id").value(userA.getId()));
    }


    @Test
    public void getAllTransactionsByAnUserId_empty() throws Exception {
        when(transactionServiceMocked.getTransactionsByUserId(anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/api/transactions/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    public void addTransaction_success() throws Exception {
        when(transactionServiceMocked.addTransaction(
                any()))
                .thenReturn(Optional.of(transaction1));

        String txJson = "{ \"id\": 1, \"amount\": 10, \"description\": \"Café\", \"fromUser\": {\"id\": 1}, \"toUser\": {\"id\": 2}}";
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(txJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transaction1.getId()));
    }


    @Test
    public void addTransaction_failed() throws Exception {
        when(transactionServiceMocked.addTransaction(
                any()))
                .thenReturn(Optional.empty());

        String txJson = "{ \"id\": 1, \"amount\": 10, \"description\": \"Café\", \"fromUser\": {\"id\": 1}, \"toUser\": {\"id\": 2}}";
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(txJson))
                .andExpect(status().isBadRequest());
    }


}