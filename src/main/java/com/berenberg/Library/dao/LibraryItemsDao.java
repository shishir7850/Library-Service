package com.berenberg.Library.dao;

import com.berenberg.Library.enums.ActionType;
import com.berenberg.Library.enums.ItemType;
import com.berenberg.Library.exception.NotFoundException;
import com.berenberg.Library.model.LibraryDataItem;
import com.berenberg.Library.model.LibraryItem;
import com.berenberg.Library.model.Transaction;
import com.berenberg.Library.model.requests.Library.BorrowItemRequest;
import com.berenberg.Library.model.requests.Library.ReturnItemRequest;
import com.berenberg.Library.utils.GlobalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.FindAndModifyOptions;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The LibraryItemsDao class serves as a data access object for managing library item-related
 * data and operations. It encapsulates interactions with the LibraryItemsRepository, which
 * is responsible for database operations on LibraryItem entities.
 */
@Component
public class LibraryItemsDao {
    private static Logger logger = LoggerFactory.getLogger(LibraryItemsDao.class);

    @Autowired
    private LibraryItemsRepository libraryItemsRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private TransactionRepository transactionRepository;

    private final Lock lockService = new ReentrantLock();

    // Function to save a LibraryItem to the database
    public LibraryItem save(LibraryItem libraryItem) {
        try {
            logger.info("Saving item: " + libraryItem.getTitle() + " itemType: " + libraryItem.getType() + " itemId: " + libraryItem.getUniqueId());
            libraryItemsRepository.save(libraryItem);
            return libraryItem;
        } catch (Exception e) {
            System.out.println(e);
        }
        return libraryItem;
    }

    // Function to retrieve the current inventory of library items
    public List<LibraryItem> getCurrentInventory() {
        return libraryItemsRepository.findByIsBorrowedFalse();
    }

    // Function to find a LibraryItem by its unique ID
    public LibraryItem findItem(int uniqueId) {
        return libraryItemsRepository.findByUniqueId(uniqueId);
    }

    // Function to find a LibraryItem by its title and item type
    public LibraryItem findItemByTitleAndItemType(String title, ItemType itemType) {
        return libraryItemsRepository.findFirstByTitleAndTypeAndIsBorrowed(title, itemType, false);
    }

    // Function to find and set overdue library items
    public List<LibraryItem> findAndSetOverDueItems() {
        logger.info("Finding overdue items");
        boolean lockAcquired = false;
        try {
            lockAcquired = lockService.tryLock(20, TimeUnit.SECONDS);
            if (lockAcquired) {
                LocalDate currentDate = LocalDate.now();

                BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, "LibraryItems");

                Query query = new Query(Criteria.where("dueDate").lt(currentDate).and("isOverdue").is(false));

                List<LibraryItem> overdueList = mongoTemplate.find(query, LibraryItem.class);
                for (LibraryItem overdueItem: overdueList) {
                    Update update = new Update().set("isOverdue", true);
                    bulkOps.updateOne(new Query(new Criteria("_id").is(overdueItem.getId())), update);
                }

                bulkOps.execute();
            }
            return null;
        } catch (InterruptedException e) {
            return null;
        } finally {
            if (lockAcquired) {
                lockService.unlock();
            }
        }
    }

    // Function to find library items borrowed by a user
    public List<LibraryItem> findItemsForUser(String userId) {
        return libraryItemsRepository.findByBorrowedBy(userId);
    }

    // Function to find overdue library items
    public List<LibraryItem> findOverDueItems() {
        return libraryItemsRepository.findByIsOverdue(true);
    }

    // Function to perform bulk insertion of library items
    public void bulkInsert(List<LibraryDataItem> itemsList) {
        logger.info("Clearing old data from LibraryItems table");
        transactionRepository.deleteAll();
        libraryItemsRepository.deleteAll();
        logger.info("Starting bulk insert task for input items: " + itemsList.size());
        List<LibraryItem> batchList = new ArrayList<>();
        for (LibraryDataItem item: itemsList) {
                LibraryItem libraryItem = new LibraryItem();
                libraryItem.setUniqueId(item.getUniqueId());
                libraryItem.setItemId(item.getItemId());
                libraryItem.setTitle(item.getTitle());
                libraryItem.setType(item.getType());
                libraryItem.setIsBorrowed(false);
                batchList.add(libraryItem);

                if (batchList.size() == GlobalConstants.batchSize) {
                    batchInsert(batchList);
                    batchList.clear();
                }
        }

        if (!batchList.isEmpty()) {
            batchInsert(batchList);
        }
    }

    // Function to perform batch insertion of library items
    private void batchInsert(List<LibraryItem> batchList) {
        logger.info("Adding library items with batch size: " + batchList.size());
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, LibraryItem.class);
        for (LibraryItem item : batchList) {
            bulkOperations.insert(item);
        }
        bulkOperations.execute();
    }

    // Function to borrow a library item
    public LibraryItem borrowItem(BorrowItemRequest request) {
        logger.info("Borrow Item");
        boolean lockAcquired = false;
        try {
            lockAcquired = lockService.tryLock(10, TimeUnit.SECONDS);
            if (lockAcquired) {
                LibraryItem borrowSuccessItem = findAndUpdateFirstAvailable(request, request.getUserId());
                if (borrowSuccessItem != null) {
                    Transaction borrowTransaction = new Transaction();
                    borrowTransaction.setUniqueItemId(borrowSuccessItem.getUniqueId());
                    borrowTransaction.setActionType(ActionType.BORROW);
                    borrowTransaction.setUserId(request.getUserId());
                    borrowTransaction.setTransactionTime(Instant.now().toEpochMilli());
                    transactionDao.save(borrowTransaction);
                    return borrowSuccessItem;
                }
            }
            throw new RuntimeException("Thread failed to acquire lock");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lockAcquired) {
                lockService.unlock();
            }
        }
    }

    // Function to find and update the first available library item for borrowing
    private LibraryItem findAndUpdateFirstAvailable(BorrowItemRequest request, String userId) {
        logger.info("Find first available item to borrow: " + request.getItemName() + " " + request.getItemName());
        LibraryItem borrowItem = libraryItemsRepository.findFirstByTitleAndTypeAndIsBorrowed(request.getItemName(), request.getItemType(), false);
        if (borrowItem != null) {
            Query query = new Query(new Criteria("_id").is(borrowItem.getId()));
            Update update = new Update();
            update.set("isBorrowed", true);
            update.set("borrowedDate", LocalDate.now());
            update.set("dueDate", LocalDate.now().plusDays(7));
            update.set("borrowedBy", userId);
            update.set("isOverdue", false);
            update.set("isBorrowed", true);
            mongoTemplate.updateFirst(query, update, "LibraryItems");
            return libraryItemsRepository.findByUniqueId(borrowItem.getUniqueId());
        }

        throw new NotFoundException();
    }

    // Function to return a library item and record the transaction
    public LibraryItem returnItem(ReturnItemRequest request, LibraryItem item) {
        logger.info("Return item: " + request.getUniqueItemId() + " userId: " + request.getUserId());
        boolean lockAcquired = false;
        try {
            lockAcquired = lockService.tryLock(10, TimeUnit.SECONDS);
            if (lockAcquired) {
                LibraryItem returnedItem = returnBorrowedItem(request, item);
                if (returnedItem != null) {
                    Transaction returnTransaction = new Transaction();
                    returnTransaction.setUniqueItemId(returnedItem.getUniqueId());
                    returnTransaction.setActionType(ActionType.RETURN);
                    returnTransaction.setUserId(request.getUserId());
                    returnTransaction.setTransactionTime(Instant.now().toEpochMilli());
                    transactionDao.save(returnTransaction);
                    return returnedItem;
                }
            }
            throw new RuntimeException("Thread failed to acquire lock");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lockAcquired) {
                lockService.unlock();
            }
        }
    }

    // Helper function to mark a library item as returned and clear related information
    public LibraryItem returnBorrowedItem(ReturnItemRequest request, LibraryItem item) {
        Query query =  new Query(new Criteria("uniqueId").is(item.getUniqueId()));
        Update update = new Update();
        update.set("isBorrowed", false);
        update.set("borrowedBy", "");
        update.set("borrowedDate", null);
        update.set("dueDate", null);
        update.set("isOverdue", false);

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        LibraryItem result = mongoTemplate.findAndModify(query, update, options, LibraryItem.class, "LibraryItems");

        // Check if the update was successful
        return result;
    }
}
