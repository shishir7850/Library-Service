package com.berenberg.Library.dao;

import com.berenberg.Library.model.User;
import com.berenberg.Library.model.requests.User.CreateUserRequest;
import com.berenberg.Library.model.requests.User.DeleteUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The UserDao class serves as a data access object for managing user-related data
 * and operations. It encapsulates interactions with the UserRepository, which is
 * responsible for database operations on User entities.
 */
@Component
public class UserDao {
    @Autowired
    private UserRepository userRepository;

    // Function to find a user by their unique userEmail
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Function to find a user by their unique user ID
    public User findUserById(String userId) {
        return userRepository.findByUserId(userId);
    }

    // Function to create a new user based on a request
    public User createNewUser(CreateUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        return userRepository.save(user);
    }

    // Function to remove a user based on a request
    public void removeUser(DeleteUserRequest request) {
        userRepository.deleteByEmail(request.getEmail());
    }
}
