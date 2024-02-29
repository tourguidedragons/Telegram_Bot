package com.example.tourbot.exception;



public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}