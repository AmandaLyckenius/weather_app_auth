package com.amanda.weather_app_auth.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomUserRepository extends JpaRepository<CustomUser, UUID> {
    Optional<CustomUser> findUserByUsername(String username);
    boolean existsByUsername(String username);
    Optional<CustomUser> findByUserId(UUID userId);
    boolean existsByEmail(String email);
}
