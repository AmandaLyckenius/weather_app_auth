package com.amanda.weather_app_auth.service;

import com.amanda.weather_app_auth.dto.AdminUserResponseDTO;
import com.amanda.weather_app_auth.dto.UserLookupResponseDTO;
import com.amanda.weather_app_auth.exception.UserNotFoundException;
import com.amanda.weather_app_auth.user.CustomUser;
import com.amanda.weather_app_auth.user.CustomUserRepository;
import com.amanda.weather_app_auth.user.mapper.CustomUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final CustomUserRepository customUserRepository;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final CustomUserMapper mapper;

    public UserService(CustomUserRepository customUserRepository, CustomUserMapper mapper) {
        this.customUserRepository = customUserRepository;
        this.mapper = mapper;
    }

    public List<AdminUserResponseDTO> getAllUsers() {
        List<AdminUserResponseDTO> users = customUserRepository.findAll()
                .stream().map(
                        customUser -> mapper.toAdminDTO(customUser)

                ).toList();

        log.info("Admin fetched {} users", users.size());

        return users;
    }

    public void deleteUserWithUsername(String username){
        String requestingUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        if (requestingUsername.equals(username)){
            log.warn("User '{}' attempted to delete themselves", requestingUsername);
            throw new IllegalArgumentException("You cannot delete yourself");
        }

        CustomUser userToDelete = customUserRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        customUserRepository.delete(userToDelete);
    }

    public UserLookupResponseDTO getUserById(UUID id) {

        log.debug("Fetching internal user info for id {}", id);

        CustomUser user = customUserRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Internal lookup failed: user with id '{}' not found", id);
                    return new UserNotFoundException(id);
                });

        log.info("Internal user lookup success for id {}", id);

        return new UserLookupResponseDTO(
                user.getEmail()
        );
    }
}
