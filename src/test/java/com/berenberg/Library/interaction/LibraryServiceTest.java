package com.berenberg.Library.interaction;

import com.berenberg.Library.dao.LibraryItemsRepository;
import com.berenberg.Library.dao.TransactionRepository;
import com.berenberg.Library.dao.UserRepository;
import com.berenberg.Library.enums.ActionType;
import com.berenberg.Library.enums.ItemType;
import com.berenberg.Library.model.LibraryDataItem;
import com.berenberg.Library.model.LibraryItem;
import com.berenberg.Library.model.Transaction;
import com.berenberg.Library.model.User;
import com.berenberg.Library.model.requests.Library.BorrowItemRequest;
import com.berenberg.Library.model.requests.Library.ItemAvailabilityRequest;
import com.berenberg.Library.model.requests.Library.ReturnItemRequest;
import com.berenberg.Library.service.OverdueItemSchedulerService;
import com.berenberg.Library.servicerequests.LibraryServiceRequests;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.berenberg.Library.model.TestDataObjects.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LibraryServiceTest {
    private static Logger logger = LoggerFactory.getLogger(LibraryServiceTest.class);

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

    private String baseUrl;

    @Autowired
    private OverdueItemSchedulerService overdueItemSchedulerService;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        baseUrl = "http://localhost:" + port + "/library";
        libraryItemsRepository.deleteAll();
        userRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    @Test
    public void testInitInventoryWhenEmpty() throws JsonProcessingException {
        List<LibraryDataItem> items = new ArrayList<>();
        ResponseEntity<String> responseEntity = libraryServiceRequests.init(baseUrl, items);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void testInitInventoryWhenValid() throws JsonProcessingException {
        ResponseEntity<String> responseEntity = libraryServiceRequests.init(baseUrl, items);

        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assert responseEntity.getBody().contains("Items added successfully: 2");
    }

    @Test
    public void getInventoryWhenEmpty() {
        ResponseEntity<Map<ItemType, List<LibraryItem>>> responseEntity = libraryServiceRequests.getInventory(baseUrl);

        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    public void getInventoryWhenValid() throws JsonProcessingException {
        //Populate Data
        testInitInventoryWhenValid();

        ResponseEntity<Map<ItemType, List<LibraryItem>>> responseEntity = libraryServiceRequests.getInventory(baseUrl);

        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assertEquals(2, responseEntity.getBody().size());
    }

    @Test
    public void borrowWhenInvalidItemName() {
        BorrowItemRequest borrowItemRequest = new BorrowItemRequest();
        ResponseEntity<LibraryItem> responseEntity = libraryServiceRequests.borrowItem(baseUrl, new BorrowItemRequest());
        assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void borrowWhenInvalidUserId() throws JsonProcessingException {
        //Populate Data
        testInitInventoryWhenValid();
        BorrowItemRequest borrowItemRequest = new BorrowItemRequest();
        borrowItemRequest.setItemName(item1.getTitle());

        ResponseEntity<LibraryItem> responseEntity = libraryServiceRequests.borrowItem(baseUrl, new BorrowItemRequest());
        assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCode().value());
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

        ResponseEntity<LibraryItem> responseEntity = libraryServiceRequests.borrowItem(baseUrl, borrowItemRequest);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assertEquals(user.getUserId(), responseEntity.getBody().getBorrowedBy());
        assertEquals(true, responseEntity.getBody().getIsBorrowed());
        assertEquals(responseEntity.getBody().getBorrowedDate().plusDays(7), responseEntity.getBody().getDueDate());

        List<Transaction> transactions = transactionRepository.findAll();
        assertEquals(ActionType.BORROW, transactions.get(0).getActionType());
    }


    @Test
    public void returnInvalidItem() {
        ResponseEntity<LibraryItem> responseEntity = libraryServiceRequests.returnItem(baseUrl, new ReturnItemRequest());
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());

    }

    @Test
    public void testReturnItemWhenInvalidUserId() throws JsonProcessingException {
        // Borrow the item
        borrowValid();

        ReturnItemRequest returnItemRequest = new ReturnItemRequest();
        List<Transaction> transactions = transactionRepository.findAll();
        returnItemRequest.setUniqueItemId(transactions.get(0).getUniqueItemId());

        ResponseEntity<LibraryItem> responseEntity = libraryServiceRequests.returnItem(baseUrl, returnItemRequest);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

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

    @Test
    public void testOverdueItemsWhenNoneOverdue() throws JsonProcessingException {
        // Populate the data
        testInitInventoryWhenValid();

        ResponseEntity<List<LibraryItem>> responseEntity = libraryServiceRequests.getOverdue(baseUrl);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    public void testOverdueServiceFunctionality() {
        for (LibraryItem libraryItem: overdueList) {
            libraryItem.setIsOverdue(false);
            libraryItemsRepository.save(libraryItem);
        }
        // Call Scheduler Service
        overdueItemSchedulerService.checkOverdueStatus();
        ResponseEntity<List<LibraryItem>> responseEntity = libraryServiceRequests.getOverdue(baseUrl);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assertEquals(2, responseEntity.getBody().size());
    }

    @Test
    public void testAvailabilityInvalid() {
        ResponseEntity<LibraryItem> responseEntity = libraryServiceRequests.getAvailable(baseUrl, new ItemAvailabilityRequest());
        assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void testAvailabilityItemFound() throws JsonProcessingException {
        testInitInventoryWhenValid();
        ItemAvailabilityRequest itemAvailabilityRequest = new ItemAvailabilityRequest();
        itemAvailabilityRequest.setItemType(item1.getType());
        itemAvailabilityRequest.setItemName(item1.getTitle());
        ResponseEntity<LibraryItem> responseEntity = libraryServiceRequests.getAvailable(baseUrl, itemAvailabilityRequest);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assertEquals(item1.getItemId(), responseEntity.getBody().getItemId());
    }

    @Test
    public void testBorrowedItemsFromUserWhenUserInvalid() {
        ResponseEntity<String> responseEntity = libraryServiceRequests.getBorrowedForUserError(baseUrl, "123");
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void testBorrowedItemsFromUserWhenUserValid() throws JsonProcessingException {
        // Populate and Borrow
        borrowValid();

        List<Transaction> transactions = transactionRepository.findAll();
        String userId = transactions.get(0).getUserId();

        ResponseEntity<Map<ItemType, List<LibraryItem>>> responseEntity = libraryServiceRequests.getBorrowedForUser(baseUrl, userId);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assertEquals(1, responseEntity.getBody().size());
    }
}
