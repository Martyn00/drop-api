package com.controller;

import com.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@CrossOrigin(origins = "http://localhost:4200")
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

    @ExceptionHandler(UsernameNotFoundException.class)
    private ResponseEntity<Object> handleUserNameNotFoundException(UsernameNotFoundException exception) {
        return createResponseBody(exception.getMessage(), HttpStatus.UNAUTHORIZED, Collections.emptyList());
    }

    @ExceptionHandler(AuthorizationException.class)
    private ResponseEntity<Object> handleUserNameNotFoundException(AuthorizationException exception) {
        return createResponseBody(exception.getMessage(), HttpStatus.UNAUTHORIZED, Collections.emptyList());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    private ResponseEntity<Object> handleUserNameNotFoundException(ExpiredJwtException exception) {
        return createResponseBody(exception.getMessage(), HttpStatus.UNAUTHORIZED, Collections.emptyList());
    }
}
