package com.berenberg.Library.model.requests.Library;

import com.berenberg.Library.enums.ItemType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemAvailabilityRequest {
    private String itemName;
    private ItemType itemType;
}
