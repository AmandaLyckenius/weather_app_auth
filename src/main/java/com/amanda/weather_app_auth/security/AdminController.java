package com.amanda.weather_app_auth.security;

import com.amanda.weather_app_auth.dto.AdminUserResponseDTO;
import com.amanda.weather_app_auth.user.CustomUser;
import com.amanda.weather_app_auth.user.CustomUserRepository;
import com.amanda.weather_app_auth.user.mapper.CustomUserMapper;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final CustomUserRepository customUserRepository;
    private final CustomUserMapper mapper;

    public AdminController(CustomUserRepository customUserRepository, CustomUserMapper mapper) {
        this.customUserRepository = customUserRepository;
        this.mapper = mapper;
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponseDTO>> getAllUsers(){
        List<AdminUserResponseDTO> users = customUserRepository.findAll()
                .stream().map(
                        customUser -> mapper.toAdminDTO(customUser)

                ).toList();

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<String> deleteUserWithUsername(@PathVariable String username){

        String requestingUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        if (requestingUsername.equals(username)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You cannot delete yourself");
        }

        CustomUser userToDelete = customUserRepository.findUserByUsername(username)
                .orElse(null);

        if (userToDelete == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User '" + username + "' not found");
        }

        customUserRepository.delete(userToDelete);

        return ResponseEntity.ok("User '" + username + "' was successfully deleted");

    }

}
