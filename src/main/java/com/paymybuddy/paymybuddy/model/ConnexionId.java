package com.paymybuddy.paymybuddy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class ConnexionId implements Serializable {
    private Integer fromUser;
    private Integer toUser;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnexionId that = (ConnexionId) o;
        return fromUser == that.fromUser &&
                toUser == that.toUser;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromUser, toUser);
    }
}
