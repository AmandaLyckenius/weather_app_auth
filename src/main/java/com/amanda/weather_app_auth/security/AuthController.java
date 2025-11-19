package com.amanda.weather_app_auth.security;

import com.amanda.weather_app_auth.dto.CustomUserCreationDTO;
import com.amanda.weather_app_auth.dto.CustomUserLoginDTO;
import com.amanda.weather_app_auth.dto.CustomUserResponseDTO;
import com.amanda.weather_app_auth.user.CustomUser;
import com.amanda.weather_app_auth.user.CustomUserRepository;
import com.amanda.weather_app_auth.user.authority.UserRole;
import com.amanda.weather_app_auth.user.mapper.CustomUserMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CustomUserRepository customUserRepository;
    private final CustomUserMapper customUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(CustomUserRepository customUserRepository, CustomUserMapper customUserMapper, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.customUserRepository = customUserRepository;
        this.customUserMapper = customUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomUserResponseDTO> register(@RequestBody @Valid CustomUserCreationDTO dto){

        if (customUserRepository.existsByUsername(dto.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        CustomUser user = customUserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setUserRole(UserRole.USER);

        CustomUser saved = customUserRepository.save(user);

        CustomUserResponseDTO responseDTO = customUserMapper.toResponseDTO(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid CustomUserLoginDTO dto){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.username(),
                        dto.password()
                )
        );

        if (authentication.isAuthenticated()){
            return ResponseEntity.ok("Login successfull");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


}
