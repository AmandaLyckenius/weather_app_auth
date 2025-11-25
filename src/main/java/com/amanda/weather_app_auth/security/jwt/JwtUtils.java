package com.amanda.weather_app_auth.security.jwt;

import com.amanda.weather_app_auth.user.CustomUser;
import com.amanda.weather_app_auth.user.authority.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private final SecretKey key;

    private final long jwtExpirationMs;

    public JwtUtils(@Value("${jwt.secret}") String jwtSecret, @Value("${jwt.expiration-ms}") long expirationMs) {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpirationMs = expirationMs;
    }

    public String generateJwtToken(CustomUser customUser) {
        logger.debug("Generating JWT for user: {} with role: {}",
                customUser.getUsername(), customUser.getUserRole());

        String roleName = customUser.getUserRole().getRoleName();

        List<String> roles = List.of(roleName);

        String token = Jwts.builder()
                .subject(customUser.getUsername())
                .claim("authorities", roles)
                .claim("userId", customUser.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();

        logger.info("JWT generated successfully for user: {}", customUser.getUsername());
        return token;
    }

    public String getUsernameFromJwtToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            logger.debug("Extracted username '{}' from JWT token", username);
            return username;
        } catch (Exception e) {
            logger.warn("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    public Set<UserRole> getRolesFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        List<?> authoritiesClaim = claims.get("authorities", List.class);
        if (authoritiesClaim == null || authoritiesClaim.isEmpty()) {
            logger.warn("No authorities found in JWT token");
            return Set.of();
        }

        Set<UserRole> roles = authoritiesClaim.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(String::toUpperCase)
                .map(roleString -> {

                    String enumName = roleString.replace("ROLE_", "");
                    return UserRole.valueOf(enumName);
                })
                .collect(java.util.stream.Collectors.toSet());

        logger.debug("Extracted roles from JWT token: {}", roles);
        return roles;
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);
            logger.debug("JWT validation succeeded");
            return true;
        } catch (Exception e) {
            logger.error("JWT validation failed: {}", e.getMessage());
        }
        return false;
    }

    public String extractJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            logger.debug("No cookies found in request");
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                logger.debug("JWT extracted from authToken cookie");
                return cookie.getValue();
            }
        }

        logger.debug("No authToken cookie found");
        return null;
    }

    public String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logger.debug("JWT extracted from Authorization header");
            return token;
        }
        logger.debug("No Bearer token found in Authorization header");
        return null;
    }
}
