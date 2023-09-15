package com.berenberg.Library.requestprocessors.library;

import com.berenberg.Library.dao.LibraryItemsDao;
import com.berenberg.Library.enums.ItemType;
import com.berenberg.Library.model.LibraryItem;
import com.berenberg.Library.requestprocessors.AbstractRequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Component
public class GetInventoryRequestProcessor extends AbstractRequestProcessor<Void, Map<ItemType, List<LibraryItem>>> {
    @Autowired
    private LibraryItemsDao libraryItemsDao;

    @Override
    public Map<ItemType, List<LibraryItem>> processRequest(Void request) {
        List<LibraryItem> libraryList = libraryItemsDao.getCurrentInventory();
        return libraryList.stream().collect(groupingBy(LibraryItem::getType));
    }
}
