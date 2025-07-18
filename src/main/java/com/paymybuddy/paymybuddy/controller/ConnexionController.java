package com.paymybuddy.paymybuddy.controller;

import com.paymybuddy.paymybuddy.model.Connexion;
import com.paymybuddy.paymybuddy.service.ConnexionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/connexions")
@AllArgsConstructor
public class ConnexionController {

    private ConnexionService connexionService;


    @GetMapping("/{userId}")
    public ResponseEntity<Iterable<Connexion>> getAllConnexionforAnUserID(@PathVariable int userId) {
        Iterable<Connexion> connexionList = connexionService.getAllConnexionsbyUserId(userId);
        return ResponseEntity.ok(connexionList);
    }

    @PostMapping
    public ResponseEntity<Connexion> addConnexion(@RequestBody Connexion connexion) {
        Optional<Connexion> createdConnection = connexionService.addConnexion(connexion);
        if (createdConnection.isPresent()) {
            return ResponseEntity.ok(createdConnection.get());
        }
        return ResponseEntity.badRequest().build();

    }

    @DeleteMapping
    public ResponseEntity<Connexion> deleteConnexion(@RequestBody Connexion connexion) {
        Optional<Connexion> deletedConnection = connexionService.deleteConnexion(connexion);
        if (deletedConnection.isPresent()) {
            return ResponseEntity.ok(deletedConnection.get());
        }
        return ResponseEntity.badRequest().build();


    }
}
