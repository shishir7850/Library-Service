package com.berenberg.Library.requestprocessors.transaction;

import com.berenberg.Library.dao.LibraryItemsDao;
import com.berenberg.Library.dao.TransactionDao;
import com.berenberg.Library.dao.UserDao;
import com.berenberg.Library.exception.InvalidInputException;
import com.berenberg.Library.model.LibraryItem;
import com.berenberg.Library.model.Transaction;
import com.berenberg.Library.model.User;
import com.berenberg.Library.requestprocessors.AbstractRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetTransactionsForItemRequestProcessor extends AbstractRequestProcessor<String, List<Transaction>> {
    private static Logger logger = LoggerFactory.getLogger(GetTransactionForUserRequestProcessor.class);
    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private LibraryItemsDao libraryItemsDao;

    @Override
    public void validateInput(String uniqueItemId) {
        if (uniqueItemId.isEmpty()) {
            logger.info("Item not found");
            throw new InvalidInputException("Item not found");
        }

        LibraryItem libraryItem = libraryItemsDao.findItem(Integer.parseInt(uniqueItemId));
        if (libraryItem == null) {
            logger.info("Item not found");
            throw new InvalidInputException("Item not found");
        }
    }

    @Override
    public List<Transaction> processRequest(String request) {
        return transactionDao.findAllForItem(Integer.parseInt(request));
    }
}
