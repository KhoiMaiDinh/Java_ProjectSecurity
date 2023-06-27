package com.buglachuyennho.securityapp.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;


@Document("User")
@Data @NoArgsConstructor @AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;
    @NotBlank(message = "Name shouldn't be blank")
    private String name;
    private String password;
    @DBRef
    private ArrayList<Role> roles = new ArrayList<>();
    @Indexed(unique = true)
    @NotBlank(message = "username shouldn't be blank")
    @Email
    private String username;

    private String avatarUrl;

    @NotNull
    @Field("auth_provider")
    private String provider;
    public void setProvider(AuthProvider authProvider) {
        provider = authProvider.toString();
    }
    public void setProvider(String authProvider) {
        provider = authProvider;
    }

    public User(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }
}
