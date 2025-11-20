package com.amanda.weather_app_auth.exception;

public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException (String username){
        super("Username '" + username + "' already exists");
    }
}
