package com.amanda.weather_app_auth.debug;

import com.amanda.weather_app_auth.user.CustomUser;
import com.amanda.weather_app_auth.user.CustomUserRepository;
import com.amanda.weather_app_auth.user.authority.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DebugController(CustomUserRepository customUserRepository, PasswordEncoder passwordEncoder) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/create-admin")
    public ResponseEntity<String> createDebugAdmin(){

        if (customUserRepository.existsByUsername("debug_admin")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Debug admin already exists");
        }

        CustomUser admin = new CustomUser();
        admin.setUsername("debug_admin");
        admin.setPassword(passwordEncoder.encode("Benny123!"));
        admin.setEmail("debug@admin.com");

        admin.setAccountNonExpired(true);
        admin.setAccountNonLocked(true);
        admin.setCredentialsNonExpired(true);
        admin.setEnabled(true);
        admin.setUserRole(UserRole.ADMIN);

        customUserRepository.save(admin);

        return ResponseEntity.ok("Debug admin created successfully");

    }

}
