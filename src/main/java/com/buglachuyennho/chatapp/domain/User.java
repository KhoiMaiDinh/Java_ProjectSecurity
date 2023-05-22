package com.buglachuyennho.chatapp.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;


@Document("User")
@Data @NoArgsConstructor @AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;
    private String firstname;
    private String lastname;
    private String password;
    @DBRef
    private ArrayList<Role> roles = new ArrayList<>();
    @Indexed(unique = true)
    private String username;


    public User(String firstname, String lastname, String username, String password, Collection<Role> roles) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
    }
}
