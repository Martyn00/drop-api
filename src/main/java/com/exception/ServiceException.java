package com.exception;

public class ServiceException extends RuntimeException {
    public ServiceException(String messaage) {
        super(messaage);
    }
}
