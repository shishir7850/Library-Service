package com.berenberg.Library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Bad Request")
public class InvalidInputException extends RuntimeException {
    public InvalidInputException() {

    }
    public InvalidInputException(String reason) {
        super(reason);
    }
}
