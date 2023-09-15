package com.berenberg.Library.interaction;

import com.berenberg.Library.dao.UserRepository;
import com.berenberg.Library.model.User;
import com.berenberg.Library.servicerequests.UserServiceRequests;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static com.berenberg.Library.model.TestDataObjects.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserServiceTest {
    @Autowired
    private UserServiceRequests userServiceRequests;

    @Autowired
    private UserRepository userRepository;


    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        baseUrl = "http://localhost:" + port + "/user";
        userRepository.deleteAll();
    }


    @Test
    public void testCreateInvalidName() {
        ResponseEntity<User> userResponseEntity = userServiceRequests.create(baseUrl, invalidName);
        assertEquals(HttpStatus.BAD_REQUEST.value(), userResponseEntity.getStatusCode().value());
    }

    @Test
    public void testCreateInvalidEmail() {
        ResponseEntity<User> userResponseEntity = userServiceRequests.create(baseUrl, invalidEmail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), userResponseEntity.getStatusCode().value());
    }

    @Test
    public void testCreateUserValid() {
        ResponseEntity<User> userResponseEntity = userServiceRequests.create(baseUrl, validUser);
        assertEquals(HttpStatus.OK.value(), userResponseEntity.getStatusCode().value());
        assertEquals(validUser.getName(), userResponseEntity.getBody().getName());
        assertEquals(validUser.getEmail(), userResponseEntity.getBody().getEmail());
    }

    @Test
    public void testCreateUserExists() {
        testCreateUserValid();
        ResponseEntity<User> userResponseEntity = userServiceRequests.create(baseUrl, validUser);
        assertEquals(HttpStatus.BAD_REQUEST.value(), userResponseEntity.getStatusCode().value());
    }

    @Test
    public void testRemoveUserInvalid() {
        ResponseEntity<String> userResponseEntity = userServiceRequests.remove(baseUrl, removeInvalidUser);
        assertEquals(HttpStatus.BAD_REQUEST.value(), userResponseEntity.getStatusCode().value());
    }

    @Test
    public void testRemoveUserValid() {
        testCreateUserValid();
        removeValidUser.setEmail(validUser.getEmail());
        ResponseEntity<String> userResponseEntity = userServiceRequests.remove(baseUrl, removeValidUser);
        assertEquals(HttpStatus.OK.value(), userResponseEntity.getStatusCode().value());
        assertEquals("User Deleted Successfully", userResponseEntity.getBody());

    }
}
