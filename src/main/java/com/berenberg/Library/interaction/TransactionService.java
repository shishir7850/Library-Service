package com.berenberg.Library.interaction;

import com.berenberg.Library.requestprocessors.transaction.GetTransactionForUserRequestProcessor;
import com.berenberg.Library.requestprocessors.transaction.GetTransactionsForItemRequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is an API controller which handles all the transaction functions
 */
@RestController
@RequestMapping("/transaction")
public class TransactionService extends AbstractBaseService {
    @Autowired
    GetTransactionForUserRequestProcessor getTransactionForUserRequestProcessor;

    @Autowired
    GetTransactionsForItemRequestProcessor getTransactionsForItemRequestProcessor;

    /**
     * Retrieves the list of transaction by a specific user.
     *
     * @param userId The ID of the user for whom to retrieve borrowed items.
     * @return List of transactions items for the user.
     */
    @GetMapping("/user/{userId}")
    @Transactional
    public ResponseEntity<Object> getTransactionForUser(@PathVariable String userId) {
        return generateResponse(getTransactionForUserRequestProcessor, userId);
    }

    /**
     * Retrieves the list of transactions by a specific item.
     *
     * @param itemId The ID of the item to retrieve transactions.
     * @return List of transactions items of the item.
     */
    @GetMapping("/item/{itemId}")
    @Transactional
    public ResponseEntity<Object> getTransactionForItem(@PathVariable String itemId) {
        return generateResponse(getTransactionsForItemRequestProcessor, itemId);
    }
}
