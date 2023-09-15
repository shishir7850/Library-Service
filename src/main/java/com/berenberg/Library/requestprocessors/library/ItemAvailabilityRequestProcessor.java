package com.berenberg.Library.requestprocessors.library;

import com.berenberg.Library.dao.LibraryItemsDao;
import com.berenberg.Library.exception.NotFoundException;
import com.berenberg.Library.model.LibraryItem;
import com.berenberg.Library.model.requests.Library.ItemAvailabilityRequest;
import com.berenberg.Library.requestprocessors.AbstractRequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemAvailabilityRequestProcessor extends AbstractRequestProcessor<ItemAvailabilityRequest, LibraryItem> {
    @Autowired
    private LibraryItemsDao libraryItemsDao;

    @Override
    public void validateInput(ItemAvailabilityRequest request) {
        LibraryItem libraryItem = libraryItemsDao.findItemByTitleAndItemType(request.getItemName(), request.getItemType());
        if (libraryItem == null) {
            throw new NotFoundException();
        }
    }
    @Override
    public LibraryItem processRequest(ItemAvailabilityRequest request) {
        return libraryItemsDao.findItemByTitleAndItemType(request.getItemName(), request.getItemType());
    }
}
