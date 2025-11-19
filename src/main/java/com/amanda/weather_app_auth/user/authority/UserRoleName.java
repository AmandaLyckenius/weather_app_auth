package com.amanda.weather_app_auth.user.authority;

public enum UserRoleName {
    GUEST("ROLE_GUEST"),
    USER("ROLE_USER");

    private final String roleName;


    UserRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
