package com.berenberg.Library.requestprocessors.library;

import com.berenberg.Library.dao.LibraryItemsDao;
import com.berenberg.Library.model.LibraryItem;
import com.berenberg.Library.requestprocessors.AbstractRequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetOverdueItemsRequestProcessor extends AbstractRequestProcessor<Void, List<LibraryItem>> {
    @Autowired
    private LibraryItemsDao libraryItemsDao;
    @Override
    public List<LibraryItem> processRequest(Void request) {
        return libraryItemsDao.findOverDueItems();
    }
}
