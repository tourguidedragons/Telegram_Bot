package com.example.tourbot.exception;



public class ActiveSessionNotFoundException extends RuntimeException {
    public ActiveSessionNotFoundException(String message) {
        super(message);
    }
}