package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.model.Connexion;
import com.paymybuddy.paymybuddy.model.ConnexionId;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.ConnexionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConnexionServiceTest {

    @Mock
    private ConnexionRepository connexionRepoMocked;
    @Mock
    private UserService userServiceMocked;
    private ConnexionService serviceUnderTest;

    private User userA;
    private User userB;
    private Connexion connexionAB;
    private List<Connexion> connexionMockedList;

    @BeforeEach
    void setUp() {
        userA = User.builder()
                .id(1).userName("alice").email("alice@mail.com").password("alicepwd").connexions(new ArrayList<>()).build();
        userB = User.builder()
                .id(2).userName("bob").email("bob@mail.com").password("bobpwd").connexions(new ArrayList<>()).build();
        connexionAB = Connexion.builder()
                .fromUser(userA.getId())
                .toUser(userB.getId())
                .build();
        connexionMockedList = List.of(connexionAB);


        serviceUnderTest = new ConnexionService(connexionRepoMocked, userServiceMocked);
    }


    @Test
    public void getAllConnexionsbyUserId__returnsFilteredList() {
        when(connexionRepoMocked.findAllByFromUser(anyInt())).thenReturn(connexionMockedList);

        Iterable<Connexion> result = serviceUnderTest.getAllConnexionsbyUserId(userA.getId());
        List<Connexion> resultList = new ArrayList<>();
        result.forEach(resultList::add);

        assertEquals(1, resultList.size());
        assertEquals(connexionAB, resultList.get(0));
    }

    @Test
    public void getAllConnexionsbyUserId_returnsEmptyList() {
        when(connexionRepoMocked.findAllByFromUser(anyInt())).thenReturn(List.of());


        Iterable<Connexion> result = serviceUnderTest.getAllConnexionsbyUserId(userA.getId());
        List<Connexion> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        assertTrue(resultList.isEmpty());
    }

    @Test
    public void getConnexionByUsersIds_found() {
        ConnexionId id = new ConnexionId(userA.getId(), userB.getId());
        when(connexionRepoMocked.findById(id)).thenReturn(Optional.of(connexionAB));
        Optional<Connexion> result = serviceUnderTest.getConnexionByUsersIds(userA.getId(), userB.getId());
        assertTrue(result.isPresent());
        assertEquals(connexionAB, result.get());
    }

    @Test
    public void getConnexionByUsersIds_notFound() {
        ConnexionId id = new ConnexionId(userA.getId(), userB.getId());
        when(connexionRepoMocked.findById(id)).thenReturn(Optional.empty());
        Optional<Connexion> result = serviceUnderTest.getConnexionByUsersIds(userA.getId(), userB.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    public void addConnexion_SaveAndReturn() {
        when(connexionRepoMocked.save(connexionAB)).thenReturn(connexionAB);
        when(userServiceMocked.getUserById(1)).thenReturn(Optional.of(userA));
        when(userServiceMocked.getUserById(2)).thenReturn(Optional.of(userB));
        Optional<Connexion> result = serviceUnderTest.addConnexion((connexionAB));
        assertEquals(connexionAB, result.get());
        verify(connexionRepoMocked).save(connexionAB);
    }

    @Test
    public void deleteConnexion_succeed_shouldReturnPresent() {
        ConnexionId id = new ConnexionId(userA.getId(), userB.getId());


        when(connexionRepoMocked.existsById(id)).thenReturn(true, false);

        doNothing().when(connexionRepoMocked).deleteById(id);

        Optional<Connexion> deleted = serviceUnderTest.deleteConnexion(connexionAB);


        assertTrue(deleted.isPresent());


        verify(connexionRepoMocked).deleteById(id);


        verify(connexionRepoMocked, times(2)).existsById(id);
    }

    @Test
    public void deleteConnexion_notSucceed_shouldReturnEmpty() {
        ConnexionId id = new ConnexionId(userA.getId(), userB.getId());


        when(connexionRepoMocked.existsById(id)).thenReturn(true, true);


        doNothing().when(connexionRepoMocked).deleteById(id);

        Optional<Connexion> deleted = serviceUnderTest.deleteConnexion(connexionAB);
        assertTrue(deleted.isEmpty());
        verify(connexionRepoMocked).deleteById(id);
        verify(connexionRepoMocked, times(2)).existsById(id);
    }
}
