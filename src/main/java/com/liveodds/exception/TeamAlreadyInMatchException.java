package com.liveodds.exception;

public class TeamAlreadyInMatchException extends RuntimeException {
    public TeamAlreadyInMatchException(String message) {
        super(message);
    }
}
