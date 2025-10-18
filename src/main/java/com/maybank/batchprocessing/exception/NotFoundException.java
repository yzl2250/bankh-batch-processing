package com.maybank.batchprocessing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException forEntity(String entityName, Object id) {
        return new NotFoundException(entityName + " not found for id " + id);
    }
}