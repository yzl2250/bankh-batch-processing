package com.maybank.batchprocessing.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_ShouldReturn404() {
        NotFoundException ex = new NotFoundException("Item not found");
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Item not found", response.getBody().get("message"));
    }

    @Test
    void handleOptimisticLock_ShouldReturn409() {
        OptimisticLockingFailureException ex = new OptimisticLockingFailureException("Conflict");
        ResponseEntity<Map<String, Object>> response = handler.handleOptimisticLock(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Concurrent update detected, please retry", response.getBody().get("message"));
    }

    @Test
    void handleDatabaseError_ShouldReturn503() {
        DataAccessException ex = new DataAccessResourceFailureException("DB error");
        ResponseEntity<Map<String, Object>> response = handler.handleDatabaseError(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Database connection failed. Please try again later.", response.getBody().get("message"));
    }

    @Test
    void handleAll_ShouldReturn500() {
        Exception ex = new Exception("Unexpected error");
        ResponseEntity<Map<String, Object>> response = handler.handleAll(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error", response.getBody().get("message"));
    }
}