package com.amanda.weather_app_auth.controller;

import com.amanda.weather_app_auth.dto.UserLookupResponseDTO;
import com.amanda.weather_app_auth.service.UserService;
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

    private final UserService userService;
    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    //Behövs inte vid rabbitMQ
    @GetMapping("/{id}")
    public ResponseEntity<UserLookupResponseDTO> getUserById(@PathVariable UUID id){
        UserLookupResponseDTO internalUserDTO = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(internalUserDTO);
    }
}
