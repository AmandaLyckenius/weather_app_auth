package com.amanda.weather_app_auth.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String username){
        super("User '" + username + "' not found");
    }
    public UserNotFoundException (UUID id){
        super("User with user id "+ id + " not found");
    }
}
