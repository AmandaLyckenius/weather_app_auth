package com.amanda.weather_app_auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomUserCreationDTO {

    @NotBlank(message = "Username can not be empty or blank")
            @Size(min= 2, max = 50, message = "Username length should be between 2-50 chars")
    String username;

    @Pattern(
            regexp = "^" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[0-9])" +
                    "(?=.*[ @$!%*?&])" +
                    ".+$",
            message = "Password must contain at least one uppercase, one lowercase, one digit, and one special character"
    )
    @Size(max = 80, message = "Maximum length of password exceeded")
    String password;

    @Email(message = "Email must be a valid email address")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Invalid email format"
    )
    @NotBlank (message = "Email can not be empty or blank")
    String email;
}
