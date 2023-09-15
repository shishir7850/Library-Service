package com.berenberg.Library.requestprocessors.library;

import com.berenberg.Library.dao.LibraryItemsDao;
import com.berenberg.Library.dao.UserDao;
import com.berenberg.Library.exception.NotFoundException;
import com.berenberg.Library.model.LibraryItem;
import com.berenberg.Library.model.User;
import com.berenberg.Library.model.requests.Library.BorrowItemRequest;
import com.berenberg.Library.requestprocessors.AbstractRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BorrowItemRequestProcessor extends AbstractRequestProcessor<BorrowItemRequest, LibraryItem> {
    private static Logger logger = LoggerFactory.getLogger(BorrowItemRequestProcessor.class);

    @Autowired
    private LibraryItemsDao libraryItemsDao;

    @Autowired
    private UserDao userDao;

    @Override
    public void validateInput(BorrowItemRequest request) {
        LibraryItem items = libraryItemsDao.findItemByTitleAndItemType(request.getItemName(), request.getItemType());
        if (items == null) {
            logger.info("No book available");
            throw new NotFoundException("");
        }

        User user = userDao.findUserById(request.getUserId());
        if (user == null) {
            logger.error("User not found with Id: " + request.getUserId());
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public LibraryItem processRequest(BorrowItemRequest request) {
        LibraryItem borrowedItem = libraryItemsDao.borrowItem(request);
        if (borrowedItem == null) {
            throw new RuntimeException("Unable to borrow book");
        }
        return borrowedItem;
    }
}
