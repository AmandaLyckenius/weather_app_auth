package com.amanda.weather_app_auth.user.mapper;

import com.amanda.weather_app_auth.dto.CustomUserCreationDTO;
import com.amanda.weather_app_auth.dto.CustomUserResponseDTO;
import com.amanda.weather_app_auth.user.CustomUser;
import org.springframework.stereotype.Component;

@Component
public class CustomUserMapper {

    public CustomUser toEntity(CustomUserCreationDTO customUserCreationDTO){

        CustomUser user = new CustomUser();
        user.setUsername(customUserCreationDTO.username());
        user.setPassword(customUserCreationDTO.password());
        user.setEmail(customUserCreationDTO.email());

        return user;
    }

    public CustomUserResponseDTO toResponseDTO(CustomUser customUser){
        return new CustomUserResponseDTO(
                customUser.getUsername()
        );
    }

}
