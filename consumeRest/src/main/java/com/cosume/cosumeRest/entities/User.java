package com.cosume.cosumeRest.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id", nullable = false)
    private int id;

    @Column(name = "user_name", nullable = false, length = 100)
    private String name;

    // MUST override equals() & hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name='" + name + "'}";
    }
}
