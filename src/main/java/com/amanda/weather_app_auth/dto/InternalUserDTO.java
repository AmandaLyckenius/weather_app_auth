package com.amanda.weather_app_auth.dto;

import java.util.UUID;

public record InternalUserDTO(
        UUID id,
        String username,
        String email
) {
}
