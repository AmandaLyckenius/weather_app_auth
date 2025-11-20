package com.amanda.weather_app_auth.security;

import com.amanda.weather_app_auth.dto.AdminUserResponseDTO;
import com.amanda.weather_app_auth.user.CustomUserRepository;
import com.amanda.weather_app_auth.user.mapper.CustomUserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
