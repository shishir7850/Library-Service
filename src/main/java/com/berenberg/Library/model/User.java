package com.berenberg.Library.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The User class represents a user entity in the application.
 * The User class is mapped to a MongoDB collection named "user"
 * name - Name of User
 * email - Email of User
 */
@Getter
@Setter
@Document(collection = "user")
public class User {
    @Id
    private String userId;
    private String name;
    @Indexed(unique = true)
    private String email;
}
