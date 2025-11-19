package com.amanda.weather_app_auth.dto;

public record CustomUserLoginDTO(
        String password,
        String username
) {
}
