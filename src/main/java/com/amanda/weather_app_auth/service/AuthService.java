package com.amanda.weather_app_auth.service;

import com.amanda.weather_app_auth.dto.CustomUserCreationDTO;
import com.amanda.weather_app_auth.dto.CustomUserLoginDTO;
import com.amanda.weather_app_auth.dto.CustomUserLoginResponseDTO;
import com.amanda.weather_app_auth.dto.CustomUserResponseDTO;
import com.amanda.weather_app_auth.exception.EmailAlreadyExistsException;
import com.amanda.weather_app_auth.exception.UserNotFoundException;
import com.amanda.weather_app_auth.exception.UsernameAlreadyExistsException;
import com.amanda.weather_app_auth.security.jwt.JwtUtils;
import com.amanda.weather_app_auth.user.CustomUser;
import com.amanda.weather_app_auth.user.CustomUserDetails;
import com.amanda.weather_app_auth.user.CustomUserRepository;
import com.amanda.weather_app_auth.user.authority.UserRole;
import com.amanda.weather_app_auth.user.mapper.CustomUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final CustomUserRepository customUserRepository;
    private final CustomUserMapper customUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;


    public AuthService(CustomUserRepository customUserRepository, CustomUserMapper customUserMapper, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.customUserRepository = customUserRepository;
        this.customUserMapper = customUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public CustomUserResponseDTO register(CustomUserCreationDTO dto) {

        if (customUserRepository.existsByUsername(dto.username())) {
            log.warn("Attempt to register with existing username '{}'", dto.username());
            throw new UsernameAlreadyExistsException(dto.username());
        }

        if (customUserRepository.existsByEmail(dto.email())){
            log.warn("Attempt to register with existing email '{}'", dto.email());
            throw new EmailAlreadyExistsException(dto.email());
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

        return customUserMapper.toResponseDTO(saved);
    }

    public CustomUserLoginResponseDTO login(CustomUserLoginDTO dto){
        log.debug("Login attempt for username '{}'", dto.username());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.username(),
                        dto.password()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();


        String token = jwtUtils.generateJwtToken(userDetails);

        log.info("User '{}' logged in", userDetails.getUsername());

        return new CustomUserLoginResponseDTO(
                userDetails.getUsername(), token
        );
    }

}
