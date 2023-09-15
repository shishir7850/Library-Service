package com.berenberg.Library.dao;

import com.berenberg.Library.enums.ItemType;
import com.berenberg.Library.model.LibraryItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LibraryItemsRepository extends MongoRepository<LibraryItem, String> {
    public List<LibraryItem> findByIsBorrowedFalse();
    public List<LibraryItem> findByTitleAndIsBorrowed(String title, boolean isBorrowed);
    public List<LibraryItem> findByDueDateLessThanAndIsBorrowed(LocalDate curdate, boolean isBorrowed);
    public LibraryItem findByUniqueId(int uniqueId);

    public LibraryItem findFirstByTitleAndTypeAndIsBorrowed(String title, ItemType itemType, boolean isBorrowed);

    public List<LibraryItem> findByBorrowedBy(String userId);

    public List<LibraryItem> findByIsOverdue(boolean isOverdue);
}
