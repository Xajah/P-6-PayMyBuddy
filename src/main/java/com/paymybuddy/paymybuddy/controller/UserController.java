package com.paymybuddy.paymybuddy.controller;


import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable int userId) {
        Optional<User> result = userService.getUserById(userId);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {

        return userService.addUser(user).map(ResponseEntity::ok).orElse(ResponseEntity.badRequest().build());

    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {

        return userService.updateUser(user).map(ResponseEntity::ok).orElse(ResponseEntity.badRequest().build());
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Integer> deleteUser(@PathVariable int userId) {
        Boolean result = userService.deleteUser(userId);
        if (result) {
            return ResponseEntity.ok(userId);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


}
