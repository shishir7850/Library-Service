package com.berenberg.Library.dao;

import com.berenberg.Library.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The TransactionDao class serves as a data access object for managing transaction-related
 * data and operations. It encapsulates interactions with the TransactionRepository, which
 * is responsible for database operations on Transaction entities.
 */
@Component
public class TransactionDao {
    @Autowired
    private TransactionRepository transactionRepository;

    public void save(Transaction transaction) {
        transactionRepository.insert(transaction);
    }

    // Find all transaction for a given user
    public List<Transaction> findAllForUser(String userId) {
        return transactionRepository.findByUserId(userId);
    }

    // Find all transactions of a given unique item id
    public List<Transaction> findAllForItem(Integer uniqueItemId) {
        return transactionRepository.findByUniqueItemId(uniqueItemId);
    }
}
