package com.berenberg.Library.model.requests.Library;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnItemRequest {
    private String userId;
    private int uniqueItemId;
}
