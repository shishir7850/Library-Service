package com.berenberg.Library.requestprocessors.library;

import com.berenberg.Library.dao.LibraryItemsDao;
import com.berenberg.Library.dao.UserDao;
import com.berenberg.Library.enums.ItemType;
import com.berenberg.Library.exception.InvalidInputException;
import com.berenberg.Library.model.LibraryItem;
import com.berenberg.Library.model.User;
import com.berenberg.Library.requestprocessors.AbstractRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Component
public class GetBorrowedItemsForUserRequestProcessor extends AbstractRequestProcessor<String, Map<ItemType, List<LibraryItem>>> {
    private static Logger logger = LoggerFactory.getLogger(GetBorrowedItemsForUserRequestProcessor.class);
    @Autowired
    private UserDao userDao;

    @Autowired
    private LibraryItemsDao libraryItemsDao;

    @Override
    public void validateInput(String userId) {
        User user = userDao.findUserById(userId);
        if (user == null) {
            logger.error("User not found");
            throw new InvalidInputException("User not found");
        }
    }

    @Override
    public Map<ItemType, List<LibraryItem>> processRequest(String userId) {
        List<LibraryItem> itemsList = libraryItemsDao.findItemsForUser(userId);
        return itemsList.stream().collect(groupingBy(LibraryItem::getType));
    }
}
