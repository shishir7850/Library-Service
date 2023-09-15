package com.berenberg.Library.interaction;

import com.berenberg.Library.dao.LibraryItemsRepository;
import com.berenberg.Library.dao.TransactionRepository;
import com.berenberg.Library.dao.UserRepository;
import com.berenberg.Library.enums.ActionType;
import com.berenberg.Library.model.LibraryItem;
import com.berenberg.Library.model.Transaction;
import com.berenberg.Library.model.User;
import com.berenberg.Library.model.requests.Library.BorrowItemRequest;
import com.berenberg.Library.model.requests.Library.ReturnItemRequest;
import com.berenberg.Library.servicerequests.LibraryServiceRequests;
import com.berenberg.Library.servicerequests.TransactionServiceRequests;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.berenberg.Library.model.TestDataObjects.*;
import static com.berenberg.Library.model.TestDataObjects.item1;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TransactionServiceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private LibraryServiceRequests libraryServiceRequests;

    @Autowired
    private LibraryItemsRepository libraryItemsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionServiceRequests transactionServiceRequests;

    private String baseUrl;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        baseUrl = "http://localhost:" + port;
        libraryItemsRepository.deleteAll();
        userRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    /**
     * Tests the scenario when attempting to find a transaction for an invalid user ID.
     */
    @Test
    public void findTransactionForInvalidUserId() throws JsonProcessingException {
        borrowValid();
        ResponseEntity<String> responseEntity = transactionServiceRequests.getTransactionForUserError(baseUrl, "userTest.getUserId()");
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    /**
     * Tests the scenario when finding a transaction for a valid user ID.
     */
    @Test
    public void findTransactionForUserId() throws JsonProcessingException {
        borrowValid();
        ResponseEntity<List<Transaction>> responseEntity = transactionServiceRequests.getTransactionForUser(baseUrl, userTest.getUserId());
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assertEquals(1, responseEntity.getBody().size());
    }

    /**
     * Tests the scenario when attempting to find a transaction for an invalid item.
     */
    @Test
    public void findTransactionForItemInvalid() throws JsonProcessingException {
        borrowValid();
        ResponseEntity<String> responseEntity = transactionServiceRequests.getTransactionForItemError(baseUrl, 10);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    /**
     * Tests the scenario when finding a transaction for a valid item.
     */
    @Test
    public void findTransactionForItem() throws JsonProcessingException {
        borrowValid();
        ResponseEntity<List<Transaction>> responseEntity = transactionServiceRequests.getTransactionForItem(baseUrl, item1.getUniqueId());
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assertEquals(1, responseEntity.getBody().size());

    }

    // For Test Data Population
    @Test
    public void testInitInventoryWhenValid() throws JsonProcessingException {
        ResponseEntity<String> responseEntity = libraryServiceRequests.init(baseUrl + "/library", items);

        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assert responseEntity.getBody().contains("Items added successfully: 2");
    }

    @Test
    public void borrowValid() throws JsonProcessingException {
        //Populate Data
        testInitInventoryWhenValid();

        User user = userRepository.save(userTest);

        BorrowItemRequest borrowItemRequest = new BorrowItemRequest();
        borrowItemRequest.setItemName(item1.getTitle());
        borrowItemRequest.setItemType(item1.getType());
        borrowItemRequest.setUserId(user.getUserId());

        ResponseEntity<LibraryItem> responseEntity = libraryServiceRequests.borrowItem(baseUrl + "/library", borrowItemRequest);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assertEquals(user.getUserId(), responseEntity.getBody().getBorrowedBy());
        assertEquals(true, responseEntity.getBody().getIsBorrowed());
        assertEquals(responseEntity.getBody().getBorrowedDate().plusDays(7), responseEntity.getBody().getDueDate());

        List<Transaction> transactions = transactionRepository.findAll();
        assertEquals(ActionType.BORROW, transactions.get(0).getActionType());
    }

    /**
     * Tests the scenario of successfully returning an item.
     */
    @Test
    public void testReturnItemValid() throws JsonProcessingException {
        // Borrow the item
        borrowValid();

        ReturnItemRequest returnItemRequest = new ReturnItemRequest();
        List<Transaction> transactions = transactionRepository.findAll();
        returnItemRequest.setUniqueItemId(transactions.get(0).getUniqueItemId());
        returnItemRequest.setUserId(transactions.get(0).getUserId());

        ResponseEntity<LibraryItem> responseEntity = libraryServiceRequests.returnItem(baseUrl, returnItemRequest);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assertEquals(false, responseEntity.getBody().getIsBorrowed());
        assertEquals("", responseEntity.getBody().getBorrowedBy());
        assertEquals(false, responseEntity.getBody().getIsOverdue());

        transactions = transactionRepository.findAll();
        assertEquals(ActionType.RETURN, transactions.get(1).getActionType());
    }

}
