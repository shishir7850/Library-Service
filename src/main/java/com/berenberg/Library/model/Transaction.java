package com.berenberg.Library.model;

import com.berenberg.Library.enums.ActionType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * The Transaction class represents a transaction entity in the application.
 * - transactionId: A unique identifier for each transaction.
 * - uniqueItemId: An integer representing a unique itemId of the item.
 * - userId: The identifier of the user associated with the transaction.
 * - actionType: An enum representing the type of action performed in the transaction.
 * - transactionTime: A timestamp indicating when the transaction occurred.
 */
@Getter
@Setter
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String transactionId;
    private int uniqueItemId;
    private String userId;
    private ActionType actionType;
    private Long transactionTime;
}
