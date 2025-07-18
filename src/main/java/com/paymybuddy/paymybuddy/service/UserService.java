package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    @Transactional
    public Optional<User> addUser(User user) {

        if (user.getUserName() == null
                || user.getEmail() == null
                || user.getPassword() == null) {

            return Optional.empty();
        }


        if (userRepository.existsByEmail(user.getEmail())) {
            return Optional.empty();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("user");
        user.setSolde(BigDecimal.ZERO);
        return Optional.of(userRepository.save(user));
    }

    @Transactional
    public Optional<User> updateUser(User newUserData) {
        Optional<User> optional = userRepository.findById(newUserData.getId());
        if (optional.isPresent()) {
            User user = optional.get();

            if (newUserData.getUserName() != null) user.setUserName(newUserData.getUserName());
            if (newUserData.getEmail() != null) user.setEmail(newUserData.getEmail());

            if (newUserData.getPassword() != null && !newUserData.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(newUserData.getPassword()));
            }
            return Optional.of(userRepository.save(user));
        }
        return Optional.empty();
    }
    @Transactional
    public boolean deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
}
