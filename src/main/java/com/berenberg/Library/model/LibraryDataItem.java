package com.berenberg.Library.model;

import com.berenberg.Library.enums.ItemType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LibraryDataItem {
    private int uniqueId;
    private int itemId;
    private ItemType type;
    private String title;
}
