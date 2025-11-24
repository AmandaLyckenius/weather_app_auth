package com.amanda.weather_app_auth.controller;

import com.amanda.weather_app_auth.dto.AdminUserResponseDTO;
import com.amanda.weather_app_auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponseDTO>> getAllUsers(){
        List<AdminUserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<String> deleteUserWithUsername(@PathVariable String username){
        userService.deleteUserWithUsername(username);
        return ResponseEntity.ok("User '" + username + "' was successfully deleted");
    }

}
