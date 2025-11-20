package com.amanda.weather_app_auth.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomUserLoginDTO(
        @NotBlank(message = "Username must not be empty")
        String username,
        @NotBlank(message = "Password must not be empty")
        String password
) {
}
