package com.amanda.weather_app_auth;

import jakarta.persistence.*;

import java.util.UUID;

@Table(name = "users")
@Entity
public class CustomUser {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    private String username;
    private String password;
    private String email;

    public CustomUser(){}
    public CustomUser(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
