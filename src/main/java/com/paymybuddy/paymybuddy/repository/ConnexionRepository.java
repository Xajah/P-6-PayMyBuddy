package com.paymybuddy.paymybuddy.repository;

import com.paymybuddy.paymybuddy.model.Connexion;
import com.paymybuddy.paymybuddy.model.ConnexionId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnexionRepository extends CrudRepository<Connexion, ConnexionId> {
    List<Connexion> findAllByFromUser(int fromUser);
}
