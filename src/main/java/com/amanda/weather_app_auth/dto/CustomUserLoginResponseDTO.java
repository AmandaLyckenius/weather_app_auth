package com.amanda.weather_app_auth.dto;

public record CustomUserLoginResponseDTO(
        String username,
        String token
) {
}
