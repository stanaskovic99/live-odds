package com.liveodds.exception;

public class NonExistingException extends RuntimeException {
    public NonExistingException(String message) {
        super(message);
    }
}
