package com.berenberg.Library.model;

import com.berenberg.Library.enums.ItemType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

/**
 * The LibraryItem class represents an item in a library's collection that can
 * be borrowed by users.
 *
 * - id: A unique identifier for each library item.
 * - uniqueId: An integer representing a unique identifier for the item within the library.
 * - itemId: An integer representing the item's identifier.
 * - title: The title or name of the library item.
 * - type: An enum representing the type of the library item.
 * - isBorrowed: A boolean indicating whether the item is currently borrowed.
 * - borrowedBy: The identifier of the user who has borrowed the item (if it's borrowed).
 * - borrowedDate: The date when the item was borrowed (if it's borrowed).
 * - dueDate: The due date for returning the item (if it's borrowed).
 * - isOverdue: A boolean indicating whether the item is overdue (if it's borrowed).
 */
@Getter
@Setter
@Document(collection= "LibraryItems")
public class LibraryItem {
    @Id
    private String id;
    @Indexed(unique = true)
    private int uniqueId;
    private int itemId;
    private String title;
    private ItemType type;
    private Boolean isBorrowed;
    private String borrowedBy;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private Boolean isOverdue;
}
