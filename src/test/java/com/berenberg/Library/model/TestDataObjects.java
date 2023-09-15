package com.berenberg.Library.model;

import com.berenberg.Library.enums.ItemType;
import com.berenberg.Library.model.requests.User.CreateUserRequest;
import com.berenberg.Library.model.requests.User.DeleteUserRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestDataObjects {
    public static List<LibraryDataItem> items;
    public static LibraryDataItem item1;
    public static LibraryDataItem item2;
    public static User userTest;
    public static CreateUserRequest invalidEmail;
    public static CreateUserRequest invalidName;
    public static CreateUserRequest validUser;
    public static DeleteUserRequest removeValidUser;
    public static DeleteUserRequest removeInvalidUser;
    public static List<LibraryItem> overdueList;
    public static LibraryItem overDue1;
    public static LibraryItem overDue2;
    public static LibraryItem notOverdue;

    public static CreateUserRequest userRequest = new CreateUserRequest();

    static {
        populateLibraryItems();
        populateCreateUser();
        populateOverdueItems();
        populateRemoveUser();
    }

    public static void populateLibraryItems() {
        items = new ArrayList<>();
        item1 = new LibraryDataItem();
        item1.setUniqueId(1);
        item1.setItemId(101);
        item1.setType(ItemType.DVD);
        item1.setTitle("Title 2");

        item2 = new LibraryDataItem();
        item2.setUniqueId(2);
        item2.setItemId(102);
        item2.setType(ItemType.VHS);
        item2.setTitle("Title 1");

        items.add(item1);
        items.add(item2);
    }

    public static void populateCreateUser() {
        userTest = new User();
        userTest.setEmail("test@gmail.com");
        userTest.setName("TestUser");

        invalidEmail = new CreateUserRequest();
        invalidEmail.setName("213123123123");
        invalidEmail.setEmail("12321312321313");

        invalidName = new CreateUserRequest();
        invalidName.setName("");
        invalidName.setEmail("sdasdas@gmail.com");

        validUser = new CreateUserRequest();
        validUser.setName("TestUser");
        validUser.setEmail("test@gmail.com");
    }

    public static void populateOverdueItems() {
        overDue1 = new LibraryItem();
        overDue1.setUniqueId(1);
        overDue1.setDueDate(LocalDate.now().minusDays(1));
        overDue1.setIsOverdue(true);

        overDue2 = new LibraryItem();
        overDue2.setUniqueId(2);
        overDue2.setDueDate(LocalDate.now().minusDays(1));
        overDue2.setIsOverdue(true);

        notOverdue = new LibraryItem();
        notOverdue.setIsOverdue(false);
        notOverdue.setUniqueId(3);

        overdueList = new ArrayList<>();
        overdueList.add(overDue1);
        overdueList.add(overDue2);
        overdueList.add(notOverdue);
    }

    public static void populateRemoveUser() {
        removeInvalidUser = new DeleteUserRequest();
        removeInvalidUser.setEmail("2343");

        removeValidUser = new DeleteUserRequest();
    }

}
