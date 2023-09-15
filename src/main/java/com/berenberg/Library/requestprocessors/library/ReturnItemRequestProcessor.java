package com.berenberg.Library.requestprocessors.library;

import com.berenberg.Library.dao.LibraryItemsDao;
import com.berenberg.Library.exception.InvalidInputException;
import com.berenberg.Library.model.LibraryItem;
import com.berenberg.Library.model.requests.Library.ReturnItemRequest;
import com.berenberg.Library.requestprocessors.AbstractRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReturnItemRequestProcessor extends AbstractRequestProcessor<ReturnItemRequest, LibraryItem> {
    private static Logger logger = LoggerFactory.getLogger(ReturnItemRequestProcessor.class);

    @Autowired
    private LibraryItemsDao libraryItemsDao;

    @Override
    public void validateInput(ReturnItemRequest request) {
        LibraryItem item = libraryItemsDao.findItem(request.getUniqueItemId());
        if (item == null || !item.getIsBorrowed() || !item.getBorrowedBy().equals(request.getUserId())) {
            logger.error("Item Id doesn't exist or not borrowed " + request.getUniqueItemId());
            throw new InvalidInputException("Item Id doesn't exist or not borrowed " + request.getUniqueItemId());
        }
    }

    @Override
    public LibraryItem processRequest(ReturnItemRequest request) {
        LibraryItem item = libraryItemsDao.findItem(request.getUniqueItemId());
        return libraryItemsDao.returnItem(request, item);
    }
}
