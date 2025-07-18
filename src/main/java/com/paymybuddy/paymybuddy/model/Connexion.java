package com.paymybuddy.paymybuddy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "connexion")
@IdClass(ConnexionId.class)
public class Connexion {


    @Id
    @Column(name = "user_id")
    private Integer fromUser;

    @Id
    @Column(name = "connected_to_user")
    private Integer toUser;


}
