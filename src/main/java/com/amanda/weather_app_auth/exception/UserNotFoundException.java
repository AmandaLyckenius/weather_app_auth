package com.amanda.weather_app_auth.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String username){
        super("User '" + username + "' not found");
    }
}
