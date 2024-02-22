package com.example.tourbot.exception;

public class CurrentSessionNotFoundException extends RuntimeException {
    public CurrentSessionNotFoundException(String message) {
        super(message);
    }
}
