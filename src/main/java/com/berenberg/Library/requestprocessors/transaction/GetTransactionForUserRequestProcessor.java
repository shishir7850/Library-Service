package com.berenberg.Library.requestprocessors.transaction;

import com.berenberg.Library.dao.TransactionDao;
import com.berenberg.Library.dao.UserDao;
import com.berenberg.Library.exception.InvalidInputException;
import com.berenberg.Library.model.Transaction;
import com.berenberg.Library.model.User;
import com.berenberg.Library.requestprocessors.AbstractRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetTransactionForUserRequestProcessor extends AbstractRequestProcessor<String, List<Transaction>> {
    private static Logger logger = LoggerFactory.getLogger(GetTransactionForUserRequestProcessor.class);
    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private UserDao userDao;

    @Override
    public void validateInput(String userId) {
        User user = userDao.findUserById(userId);
        if (user == null) {
            logger.info("User not found");
            throw new InvalidInputException("User not found");
        }
    }

    @Override
    public List<Transaction> processRequest(String userId) {
        return transactionDao.findAllForUser(userId);
    }
}
