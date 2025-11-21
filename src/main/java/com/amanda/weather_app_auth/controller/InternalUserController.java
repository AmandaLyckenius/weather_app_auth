package com.amanda.weather_app_auth.controller;

import com.amanda.weather_app_auth.dto.InternalUserDTO;
import com.amanda.weather_app_auth.exception.UserNotFoundException;
import com.amanda.weather_app_auth.user.CustomUser;
import com.amanda.weather_app_auth.user.CustomUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final CustomUserRepository customUserRepository;

    public InternalUserController(CustomUserRepository customUserRepository) {
        this.customUserRepository = customUserRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternalUserDTO> getUserById(@PathVariable UUID id){
        CustomUser user = customUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        InternalUserDTO internalUserDTO = new InternalUserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
        
        return ResponseEntity.status(HttpStatus.OK).body(internalUserDTO);
    }
}
