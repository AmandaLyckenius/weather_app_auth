package com.amanda.weather_app_auth.user.authority;

public enum UserPermission {

    READ("READ"),
    WRITE("WRITE");

    private final String userPermission;


    UserPermission(String userPermission) {
        this.userPermission = userPermission;
    }

    public String getUserPermission() {
        return userPermission;
    }
}
