package com.berenberg.Library.interaction;

import com.berenberg.Library.model.LibraryDataItem;
import com.berenberg.Library.model.requests.Library.BorrowItemRequest;
import com.berenberg.Library.model.requests.Library.ItemAvailabilityRequest;
import com.berenberg.Library.model.requests.Library.ReturnItemRequest;
import com.berenberg.Library.requestprocessors.library.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This class is an API controller which handles all the library functions
 */
@RestController
@RequestMapping("/library")
public class LibraryService extends AbstractBaseService {
    @Autowired
    private InitLibraryRequestProcessor initLibraryRequestProcessor;

    @Autowired
    private GetInventoryRequestProcessor getInventoryRequestProcessor;

    @Autowired
    private BorrowItemRequestProcessor borrowItemRequestProcessor;

    @Autowired
    private ReturnItemRequestProcessor returnItemRequestProcessor;

    @Autowired
    private ItemAvailabilityRequestProcessor itemAvailabilityRequestProcessor;

    @Autowired
    private GetBorrowedItemsForUserRequestProcessor getBorrowedItemsForUserRequestProcessor;

    @Autowired
    private GetOverdueItemsRequestProcessor getOverdueItemsRequestProcessor;

    /**
     * Initializes the library with a list of library items.
     *
     * @param items The list of library items to initialize.
     * @return A ResponseEntity containing the result of the initialization process.
     */
    @PostMapping("/init")
    @Transactional
    public ResponseEntity<Object> initLibrary(@RequestBody List<LibraryDataItem> items) {
        return generateResponse(initLibraryRequestProcessor, items);
    }

    /**
     * Retrieves the current inventory of the library.
     *
     * @return A ResponseEntity containing the current inventory of the library.
     */
    @GetMapping("/inventory")
    @Transactional
    public ResponseEntity<Object> getInventory() {
        return generateResponse(getInventoryRequestProcessor, null);
    }

    /**
     * Borrows a library item based on the provided request.
     *
     * @param request The BorrowItemRequest specifying the item to borrow and the user.
     * @return A ResponseEntity indicating the result of the borrow operation.
     */
    @PutMapping("/borrow")
    @Transactional
    public ResponseEntity<Object> borrowItem(@RequestBody BorrowItemRequest request) {
        return generateResponse(borrowItemRequestProcessor, request);
    }

    /**
     * Returns a borrowed library item based on the provided request.
     *
     * @param request The ReturnItemRequest specifying the item to return and the user.
     * @return A ResponseEntity indicating the result of the return operation.
     */
    @PutMapping("/return")
    @Transactional
    public ResponseEntity<Object> returnBook(@RequestBody ReturnItemRequest request) {
        return generateResponse(returnItemRequestProcessor, request);
    }

    /**
     * Retrieves a list of overdue library items.
     *
     * @return A ResponseEntity containing the list of overdue items.
     */
    @GetMapping("/overdue")
    @Transactional
    public ResponseEntity<Object> getOverdueItems() {
        return generateResponse(getOverdueItemsRequestProcessor, null);
    }

    /**
     * Checks the availability of a library item based on the provided request.
     *
     * @param request The ItemAvailabilityRequest specifying the item to check and its availability.
     * @return A ResponseEntity indicating the availability status of the item. 404 if not found
     */
    @PostMapping("/available")
    @Transactional
    public ResponseEntity<Object> checkAvailability(@RequestBody ItemAvailabilityRequest request) {
        return generateResponse(itemAvailabilityRequestProcessor, request);
    }

    /**
     * Retrieves the list of library items borrowed by a specific user.
     *
     * @param userId The ID of the user for whom to retrieve borrowed items.
     * @return A ResponseEntity containing the list of borrowed items for the user.
     */
    @GetMapping("/user/items/{userId}")
    @Transactional
    public ResponseEntity<Object> getItemsForUser(@PathVariable String userId) {
        return generateResponse(getBorrowedItemsForUserRequestProcessor, userId);
    }
}
