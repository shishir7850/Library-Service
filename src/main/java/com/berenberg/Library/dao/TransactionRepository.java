package com.berenberg.Library.dao;

import com.berenberg.Library.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    public List<Transaction> findByUserId(String userId);

    public List<Transaction> findByUniqueItemId(Integer uniqueItemId);
}
