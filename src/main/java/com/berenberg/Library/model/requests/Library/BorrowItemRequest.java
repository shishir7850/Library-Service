package com.berenberg.Library.model.requests.Library;

import com.berenberg.Library.enums.ItemType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BorrowItemRequest {
    private String userId;
    private String itemName;
    private ItemType itemType;
}
