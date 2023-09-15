package com.berenberg.Library.servicerequests;

import com.berenberg.Library.model.User;
import com.berenberg.Library.model.requests.User.CreateUserRequest;
import com.berenberg.Library.model.requests.User.DeleteUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

@Component
public class UserServiceRequests {
    @Autowired
    private TestRestTemplate restTemplate;

    public ResponseEntity<User> create(String baseUrl, CreateUserRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HttpEntity with the JSON request body and headers
        HttpEntity<CreateUserRequest> requestEntity = new HttpEntity<>(request, headers);

        // Send a POST request to the /init endpoint
        ResponseEntity<User> responseEntity = restTemplate.exchange(
                baseUrl + "/create",
                HttpMethod.POST,
                requestEntity,
                User.class
        );

        return responseEntity;
    }

    public ResponseEntity<String> remove(String baseUrl, DeleteUserRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HttpEntity with the JSON request body and headers
        HttpEntity<DeleteUserRequest> requestEntity = new HttpEntity<>(request, headers);

        // Send a POST request to the /init endpoint
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                baseUrl + "/delete",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return responseEntity;
    }
}
