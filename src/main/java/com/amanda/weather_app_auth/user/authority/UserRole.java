package com.amanda.weather_app_auth.user.authority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.amanda.weather_app_auth.user.authority.UserPermission.READ;
import static com.amanda.weather_app_auth.user.authority.UserPermission.WRITE;

public enum UserRole {
    GUEST(
            UserRoleName.GUEST.getRoleName(),
            Set.of()
    ),

    USER(
            UserRoleName.USER.getRoleName(),
            Set.of(READ, WRITE)
    ),

    ADMIN(
            UserRoleName.ADMIN.getRoleName(),
            Set.of(READ, WRITE)
    );

    private final String roleName;
    private final Set<UserPermission> userPermissions;


    UserRole(String roleName, Set<UserPermission> userPermissions) {
        this.roleName = roleName;
        this.userPermissions = userPermissions;
    }

    public String getRoleName() {
        return roleName;
    }

    public Set<UserPermission> getUserPermissions() {
        return userPermissions;
    }

    public List<SimpleGrantedAuthority> getUserAuthorities(){
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        authorityList.add(new SimpleGrantedAuthority(this.roleName));

        authorityList.addAll(
                this.userPermissions.stream().map(
                        userPermission -> new SimpleGrantedAuthority(userPermission.getUserPermission())
                ).toList()
        );
        return authorityList;
    }
}
