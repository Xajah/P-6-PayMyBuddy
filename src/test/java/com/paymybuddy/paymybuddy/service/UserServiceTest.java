package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMocked;

    private UserService serviceUnderTest;
    private User alice;
    private User bob;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        alice = User.builder().id(1).userName("alice").email("alice@mail.com").password("pwdAlice").build();
        bob = User.builder().id(2).userName("bob").email("bob@mail.com").password("pwdBob").build();
        passwordEncoder = new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return "";
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return false;
            }
        };
        serviceUnderTest = new UserService(userRepositoryMocked, passwordEncoder);
    }

    @Test
    public void getUsers_should_returnAll() {
        List<User> users = Arrays.asList(alice, bob);
        when(userRepositoryMocked.findAll()).thenReturn(users);

        Iterable<User> result = serviceUnderTest.getUsers();
        List<User> list = new ArrayList<>();
        result.forEach(list::add);

        assertEquals(2, list.size());
        assertTrue(list.contains(alice));
        assertTrue(list.contains(bob));
    }

    @Test
    public void getUserById_found() {
        when(userRepositoryMocked.findById(1)).thenReturn(Optional.of(alice));
        Optional<User> result = serviceUnderTest.getUserById(1);
        assertTrue(result.isPresent());
        assertEquals(alice, result.get());
    }

    @Test
    public void getUserById_notFound() {
        when(userRepositoryMocked.findById(anyInt())).thenReturn(Optional.empty());
        Optional<User> result = serviceUnderTest.getUserById(42);
        assertTrue(result.isEmpty());
    }

    @Test
    public void addUser_shouldReturnSavedUser() {
        when(userRepositoryMocked.save(bob)).thenReturn(bob);
        Optional<User> result = serviceUnderTest.addUser(bob);
        assertTrue(result.isPresent());
        assertEquals(bob, result.get());
        assertEquals(passwordEncoder.encode(bob.getPassword()), result.get().getPassword());
        verify(userRepositoryMocked).save(bob);
    }

    @Test
    public void addUser_notSave() {

    }

    @Test
    public void updateUser_ok_shouldReturnUpdated() {
        User userUpdate = User.builder().id(1).userName("newAlice").email("new@mail.com").password("newpwd").build();
        when(userRepositoryMocked.findById(1)).thenReturn(Optional.of(alice));
        when(userRepositoryMocked.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<User> result = serviceUnderTest.updateUser(userUpdate);


        assertTrue(result.isPresent());
        assertEquals("newAlice", result.get().getUserName());
        assertEquals("new@mail.com", result.get().getEmail());
        assertEquals(passwordEncoder.encode("newpwd"), result.get().getPassword());
        verify(userRepositoryMocked).save(any(User.class));
    }

    @Test
    public void updateUser_notFound_shouldReturnNull() {
        User userUpdate = User.builder().id(100).userName("nouveau").email("nouveau@mail.com").password("nouveau").build();
        when(userRepositoryMocked.findById(100)).thenReturn(Optional.empty());

        Optional<User> result = serviceUnderTest.updateUser(userUpdate);

        assertTrue(result.isEmpty());
        verify(userRepositoryMocked, never()).save(any(User.class));
    }

    @Test
    public void deleteUser_shouldDeleteAndReturnTrue_whenUserExists() {
        when(userRepositoryMocked.existsById(1)).thenReturn(true);

        assertTrue(serviceUnderTest.deleteUser(1));
        verify(userRepositoryMocked).deleteById(1);
        verify(userRepositoryMocked).existsById(1);
    }

    @Test
    public void deleteUser_shouldReturnFalse_whenUserDoesNotExist() {
        when(userRepositoryMocked.existsById(2)).thenReturn(false);

        assertFalse(serviceUnderTest.deleteUser(2));

        verify(userRepositoryMocked, never()).deleteById(anyInt());
        verify(userRepositoryMocked).existsById(2);
    }
}

