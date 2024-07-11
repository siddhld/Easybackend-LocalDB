package com.easybackend.exception;

public class DataAlreadyExistException extends RuntimeException{
    public DataAlreadyExistException() {
        super();
    }
    public DataAlreadyExistException(String message) {
        super(message);
    }

}
