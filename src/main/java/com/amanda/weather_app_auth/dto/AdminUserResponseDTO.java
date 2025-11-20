package com.amanda.weather_app_auth.dto;

import com.amanda.weather_app_auth.user.authority.UserRole;

import java.util.UUID;

public record AdminUserResponseDTO(
        UUID id,
        String username,
        String email,
        UserRole userRole

) {
}
