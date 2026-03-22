package com.cosume.cosumeRest.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_records")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRecord {

    // PRIMARY KEY (only one allowed)
    @EmbeddedId
    private User user;

    @Column(name = "email", length = 255)
    private String email;

    // Example: use AttributeOverrides to rename the embedded columns for a specific embedding
    // Normal embedded object (NOT primary key)
   /* @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "owner_user_id")),
        @AttributeOverride(name = "name", column = @Column(name = "owner_user_name"))
    })
    private User owner;*/

}

