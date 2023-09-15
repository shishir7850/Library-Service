package com.berenberg.Library.servicerequests;

import com.berenberg.Library.enums.ItemType;
import com.berenberg.Library.model.LibraryDataItem;
import com.berenberg.Library.model.LibraryItem;
import com.berenberg.Library.model.requests.Library.BorrowItemRequest;
import com.berenberg.Library.model.requests.Library.ItemAvailabilityRequest;
import com.berenberg.Library.model.requests.Library.ReturnItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LibraryServiceRequests {

    @Autowired
    private TestRestTemplate restTemplate;

    public ResponseEntity<String> init(String baseUrl, List<LibraryDataItem> items) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HttpEntity with the JSON request body and headers
        HttpEntity<List<LibraryDataItem>> requestEntity = new HttpEntity<>(items, headers);

        // Send a POST request to the /init endpoint
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                baseUrl + "/init",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return responseEntity;
    }

    public ResponseEntity<Map<ItemType, List<LibraryItem>>> getInventory(String baseUrl) {
        // Send a POST request to the /init endpoint
        ResponseEntity<Map<ItemType, List<LibraryItem>>> responseEntity = restTemplate.exchange(
                baseUrl + "/inventory",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<ItemType, List<LibraryItem>>>() {}
        );

        return responseEntity;
    }

    public ResponseEntity<LibraryItem> borrowItem(String baseUrl, BorrowItemRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HttpEntity with the JSON request body and headers
        HttpEntity<BorrowItemRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<LibraryItem> responseEntity = restTemplate.exchange(
                baseUrl + "/borrow",
                HttpMethod.PUT,
                requestEntity,
                LibraryItem.class
        );

        return responseEntity;
    }

    public ResponseEntity<LibraryItem> returnItem(String baseUrl, ReturnItemRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HttpEntity with the JSON request body and headers
        HttpEntity<ReturnItemRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<LibraryItem> responseEntity = restTemplate.exchange(
                baseUrl + "/return",
                HttpMethod.PUT,
                requestEntity,
                LibraryItem.class
        );

        return responseEntity;
    }

    public ResponseEntity<List<LibraryItem>> getOverdue(String baseUrl) {

        ResponseEntity<List<LibraryItem>> responseEntity = restTemplate.exchange(
                baseUrl + "/overdue",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<LibraryItem>>() {
                }
        );

        return responseEntity;
    }

    public ResponseEntity<LibraryItem> getAvailable(String baseUrl, ItemAvailabilityRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HttpEntity with the JSON request body and headers
        HttpEntity<ItemAvailabilityRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<LibraryItem> responseEntity = restTemplate.exchange(
                baseUrl + "/available",
                HttpMethod.POST,
                requestEntity,
                LibraryItem.class
        );

        return responseEntity;
    }

    public ResponseEntity<String> getBorrowedForUserError(String baseUrl, String userId) {

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                baseUrl + "/user/items/" + userId,
                HttpMethod.GET,
                null,
                String.class
        );

        return responseEntity;
    }

    public ResponseEntity<Map<ItemType, List<LibraryItem>>> getBorrowedForUser(String baseUrl, String userId) {

        ResponseEntity<Map<ItemType, List<LibraryItem>>> responseEntity = restTemplate.exchange(
                baseUrl + "/user/items/" + userId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<ItemType, List<LibraryItem>>>() {
                }
        );

        return responseEntity;
    }

}
