package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.model.Connexion;
import com.paymybuddy.paymybuddy.model.ConnexionId;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.ConnexionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ConnexionService {
    @Autowired
    private ConnexionRepository connexionRepository;

    @Autowired
    private UserService userService;


    public Iterable<Connexion> getAllConnexionsbyUserId(int userId) {
        return connexionRepository.findAllByFromUser(userId);
    }

    public Optional<Connexion> getConnexionByUsersIds(int fromUserId, int toUserId) {
        return connexionRepository.findById(new ConnexionId(fromUserId, toUserId));
    }

    @Transactional
    public Optional<Connexion> addConnexion(Connexion connexion) {
        int user1Id = connexion.getFromUser();
        int user2Id = connexion.getToUser();
        Optional<User> user1 = userService.getUserById(user1Id);
        Optional<User> user2 = userService.getUserById(user2Id);
        boolean exists = connexionRepository.existsById(new ConnexionId(user1Id, user2Id));
        if (user1.isPresent() && user2.isPresent() && !exists) {
            Connexion savedConnexion = connexionRepository.save(connexion);
            user1.get().getConnexions().add(savedConnexion);
            userService.updateUser(user1.get());
            return Optional.of(savedConnexion);
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Connexion> deleteConnexion(Connexion connexion) {
        ConnexionId id = new ConnexionId(connexion.getFromUser(), connexion.getToUser());
        if (connexionRepository.existsById(id)) {
            connexionRepository.deleteById(id);
            if (!connexionRepository.existsById(id)) {
                return Optional.of(connexion);
            }
        }
        return Optional.empty();
    }

}



