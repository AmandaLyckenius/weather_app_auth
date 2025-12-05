package com.amanda.weather_app_auth.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String email) {
        super("Email already exists");
    }
}
