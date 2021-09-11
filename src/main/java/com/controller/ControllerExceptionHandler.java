package com.controller;

import com.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(FacadeException.class)
    public ResponseEntity<Object> handleNotFoundException(FacadeException exception) {
        return createResponseBody(exception.getMessage(), HttpStatus.NOT_FOUND, Collections.emptyList());
    }

    @ExceptionHandler(FolderException.class)
    public ResponseEntity<Object> handleFolderException(FolderException exception) {
        return createResponseBody(exception.getMessage(), HttpStatus.NOT_FOUND, Collections.emptyList());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentials(InvalidCredentialsException exception) {
        return createResponseBody(exception.getMessage(), HttpStatus.NOT_FOUND, Collections.emptyList());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleServiceException(ServiceException exception) {
        return createResponseBody(exception.getMessage(), HttpStatus.NOT_FOUND, Collections.emptyList());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception) {
        return createResponseBody(exception.getMessage(), HttpStatus.NOT_FOUND, Collections.emptyList());
    }

    private ResponseEntity<Object> createResponseBody(String message, HttpStatus status, Collection<String> errors) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("message", message);
        responseBody.put("errors", errors);
        return new ResponseEntity<>(responseBody, status);
    }
}
