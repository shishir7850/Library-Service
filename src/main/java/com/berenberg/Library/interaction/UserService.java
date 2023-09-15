package com.berenberg.Library.interaction;

import com.berenberg.Library.model.requests.User.CreateUserRequest;
import com.berenberg.Library.model.requests.User.DeleteUserRequest;
import com.berenberg.Library.requestprocessors.user.CreateUserRequestProcessor;
import com.berenberg.Library.requestprocessors.user.DeleteUserRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is an API controller which handles all the user functions
 */
@RestController
@RequestMapping("/user")
public class UserService extends AbstractBaseService {
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private CreateUserRequestProcessor createUserRequestProcessor;

    @Autowired
    private DeleteUserRequestProcessor deleteUserRequestProcessor;

    /**
     * This method handles the creation of a new user by processing the provided
     * CreateUserRequest and returning an appropriate ResponseEntity.
     *
     * @param createUserRequest The CreateUserRequest containing user creation data.
     * @return User class
     */
    @PostMapping("/create")
    @Transactional
    public ResponseEntity<Object> createUser(@RequestBody CreateUserRequest createUserRequest) {
        return generateResponse(createUserRequestProcessor, createUserRequest);
    }

    /**
     * This method handles the deletion of a user by processing the provided
     * DeleteUserRequest and returning an appropriate ResponseEntity.
     *
     * @param deleteUserRequest The DeleteUserRequest containing user deletion data.
     * @return String
     */
    @PostMapping("/delete")
    @Transactional
    public ResponseEntity<Object> delete(@RequestBody DeleteUserRequest deleteUserRequest) {
        return generateResponse(deleteUserRequestProcessor, deleteUserRequest);
    }
}
