package com.berenberg.Library.servicerequests;

import com.berenberg.Library.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionServiceRequests {
    @Autowired
    private TestRestTemplate restTemplate;

    public ResponseEntity<List<Transaction>> getTransactionForItem(String baseUrl, Integer itemId) {

        ResponseEntity<List<Transaction>> responseEntity = restTemplate.exchange(
                baseUrl + "/transaction/item/" + itemId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Transaction>>() {
                }
        );

        return responseEntity;
    }

    public ResponseEntity<String> getTransactionForItemError(String baseUrl, Integer itemId) {

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                baseUrl + "/transaction/item/" + itemId,
                HttpMethod.GET,
                null,
                String.class
        );

        return responseEntity;
    }

    public ResponseEntity<List<Transaction>> getTransactionForUser(String baseUrl, String userId) {

        ResponseEntity<List<Transaction>> responseEntity = restTemplate.exchange(
                baseUrl + "/transaction/user/" + userId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Transaction>>() {
                }
        );

        return responseEntity;
    }

    public ResponseEntity<String> getTransactionForUserError(String baseUrl, String userId) {

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                baseUrl + "/transaction/user/" + userId,
                HttpMethod.GET,
                null,
                String.class
        );

        return responseEntity;
    }
}
