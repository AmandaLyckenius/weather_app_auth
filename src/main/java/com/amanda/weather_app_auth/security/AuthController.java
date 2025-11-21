package com.amanda.weather_app_auth.security;

import com.amanda.weather_app_auth.dto.CustomUserCreationDTO;
import com.amanda.weather_app_auth.dto.CustomUserLoginDTO;
import com.amanda.weather_app_auth.dto.CustomUserLoginResponseDTO;
import com.amanda.weather_app_auth.dto.CustomUserResponseDTO;
import com.amanda.weather_app_auth.exception.UserNotFoundException;
import com.amanda.weather_app_auth.exception.UsernameAlreadyExistsException;
import com.amanda.weather_app_auth.security.jwt.JwtUtils;
import com.amanda.weather_app_auth.user.CustomUser;
import com.amanda.weather_app_auth.user.CustomUserRepository;
import com.amanda.weather_app_auth.user.authority.UserRole;
import com.amanda.weather_app_auth.user.mapper.CustomUserMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final JwtUtils jwtUtils;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public AuthController(CustomUserRepository customUserRepository, CustomUserMapper customUserMapper, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.customUserRepository = customUserRepository;
        this.customUserMapper = customUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomUserResponseDTO> register(@RequestBody @Valid CustomUserCreationDTO dto){

        if (customUserRepository.existsByUsername(dto.username())) {
            log.warn("Attempt to register with existing username '{}'", dto.username());
            throw new UsernameAlreadyExistsException(dto.username());
        }

        CustomUser user = customUserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setUserRole(UserRole.USER);

        CustomUser saved = customUserRepository.save(user);
        log.info("User '{}' registered", saved.getUsername());

        CustomUserResponseDTO responseDTO = customUserMapper.toResponseDTO(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

    }

    @PostMapping("/login")
    public ResponseEntity<CustomUserLoginResponseDTO> login(@RequestBody @Valid CustomUserLoginDTO dto){

        log.debug("Login attempt for username '{}'", dto.username());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.username(),
                        dto.password()
                )
        );

        CustomUser user = customUserRepository
                .findUserByUsername(dto.username())
                .orElseThrow(()-> {
                    log.warn("Login failed: user '{}' not found", dto.username());
                    return new UserNotFoundException(dto.username()}));

        String token = jwtUtils.generateJwtToken(user);

        log.info("User '{}' logged in", dto.username());

        CustomUserLoginResponseDTO responseDTO = new CustomUserLoginResponseDTO(
                user.getUsername(), token
        );

        return ResponseEntity.ok(responseDTO);
    }


}
