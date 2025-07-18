package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.model.Transaction;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private UserService userServiceMocked;
    @Mock
    private TransactionRepository transactionRepoMocked;
    private TransactionService serviceUnderTest;

    private User userA;
    private User userB;
    private Transaction transaction1;
    private Transaction transaction2;
    private List<Transaction> transactionsMockedList;
    private List<Transaction> transactionFilteredList;

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
        transaction1 = Transaction.builder()
                .id(101)
                .amount(new BigDecimal("25.00"))
                .description("Remboursement resto")
                .fromUser(userA).toUser(userB)
                .build();
        transaction2 = Transaction.builder()
                .id(102)
                .amount(new BigDecimal("10.00"))
                .description("Café")
                .fromUser(userB).toUser(userA)
                .build();
        transactionsMockedList = List.of(transaction1, transaction2);
        transactionFilteredList = List.of(transaction1);

        serviceUnderTest = new TransactionService(transactionRepoMocked, userServiceMocked);
    }

    /* --- getTransactions --- */


    /* --- getTransactionById --- */
    @Test
    public void getTransactionById_found() {
        when(transactionRepoMocked.findById(anyInt())).thenReturn(Optional.of(transaction1));
        Optional<Transaction> result = serviceUnderTest.getTransactionById(999);
        assertTrue(result.isPresent());
        assertEquals(transaction1, result.get());
    }

    @Test
    public void getTransactionById_notFound() {
        when(transactionRepoMocked.findById(anyInt())).thenReturn(Optional.empty());
        Optional<Transaction> result = serviceUnderTest.getTransactionById(42);
        assertTrue(result.isEmpty());
    }


    @Test
    public void getTransactionsByUserId_userFound_transactionsFound() {
        when(transactionRepoMocked.findAllByFromUser_Id(1)).thenReturn(transactionFilteredList);
        Iterable<Transaction> result = serviceUnderTest.getTransactionsByUserId(userA.getId());
        List<Transaction> resultList = new ArrayList<>();
        result.forEach(resultList::add);

        assertEquals(1, resultList.size());
        assertTrue(resultList.contains(transaction1));
    }


    @Test
    public void getTransactionsByUserId_noMatchingTransactions() {
        User user3 = User.builder().id(3).userName("charlie").build();
        when(transactionRepoMocked.findAllByFromUser_Id(3)).thenReturn(List.of());
        Iterable<Transaction> result = serviceUnderTest.getTransactionsByUserId(3);
        assertEquals(result, List.of());
    }

    /* --- addTransaction --- */
    @Test
    public void addTransaction_uniqueTransaction_shouldAddAndUpdateSolde() {

        User fromSpy = Mockito.spy(userA);
        User toSpy = Mockito.spy(userB);


        when(userServiceMocked.getUserById(fromSpy.getId())).thenReturn(Optional.of(fromSpy));
        when(userServiceMocked.getUserById(toSpy.getId())).thenReturn(Optional.of(toSpy));

        when(transactionRepoMocked.save(any(Transaction.class))).thenReturn(transaction1);

        Transaction newTrx = Transaction.builder()
                .id(transaction1.getId())
                .amount(new BigDecimal("10.00"))
                .fromUser(fromSpy)
                .toUser(toSpy)
                .build();

        Optional<Transaction> result = serviceUnderTest.addTransaction(newTrx);

        assertTrue(result.isPresent());
        verify(fromSpy, times(1)).subtractToSolde(new BigDecimal("10.00"));
        verify(toSpy, times(1)).addToSolde(new BigDecimal("10.00"));
    }

    @Test
    public void addTransaction_duplicateTransaction_shouldReturnEmpty() {
        when(userServiceMocked.getUserById(transaction1.getFromUser().getId())).thenReturn(Optional.empty());
        Optional<Transaction> result = serviceUnderTest.addTransaction(transaction1);
        assertTrue(result.isEmpty());
        verifyNoMoreInteractions(transactionRepoMocked);
    }
}


