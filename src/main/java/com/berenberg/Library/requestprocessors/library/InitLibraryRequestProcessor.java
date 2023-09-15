package com.berenberg.Library.requestprocessors.library;

import com.berenberg.Library.dao.LibraryItemsDao;
import com.berenberg.Library.exception.InvalidInputException;
import com.berenberg.Library.model.LibraryDataItem;
import com.berenberg.Library.requestprocessors.AbstractRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InitLibraryRequestProcessor extends AbstractRequestProcessor<List<LibraryDataItem>, String> {
    private static Logger logger = LoggerFactory.getLogger(InitLibraryRequestProcessor.class);

    @Autowired
    private LibraryItemsDao libraryItemsDao;

    @Override
    public void validateInput(List<LibraryDataItem> inputList) {
        if (inputList.isEmpty()) {
            logger.info("Input list is empty");
            throw new InvalidInputException("Input List is Empty");
        }
    }

    @Override
    public String processRequest(List<LibraryDataItem> itemsList) {
        logger.info("Sending items for bulk insert");
        libraryItemsDao.bulkInsert(itemsList);
        return "Items added successfully: " + itemsList.size();
    }
}
