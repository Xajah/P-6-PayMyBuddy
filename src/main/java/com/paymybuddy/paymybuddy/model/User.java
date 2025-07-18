package com.paymybuddy.paymybuddy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "username")
    private String userName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "solde")
    private BigDecimal solde;

    @Column(name = "role")
    private String role;



    @OneToMany(mappedBy = "fromUser")
    @JsonIgnore // Empêche la boucle Transaction->User->Transaction
    private List<Connexion> connexions = new ArrayList<>();

    @OneToMany(mappedBy = "fromUser")
    @JsonIgnore
    private List<Transaction> sendedTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "toUser")
    @JsonIgnore
    private List<Transaction> receivedTransactionsRecues = new ArrayList<>();






    public boolean addToSolde(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.solde = this.solde.add(amount);
            return true;
        } else {
            return false;
        }
    }

    public boolean subtractToSolde(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.solde = this.solde.subtract(amount);
            return true;
        } else {
            return false;
        }
    }

}
