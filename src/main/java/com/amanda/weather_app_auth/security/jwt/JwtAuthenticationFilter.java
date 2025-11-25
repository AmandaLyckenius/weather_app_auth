package com.amanda.weather_app_auth.security.jwt;

import com.amanda.weather_app_auth.user.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * Custom JWT authentication filter.
 * Extracts the JWT from HttpOnly cookies (or Authorization header as fallback),
 * validates the token, loads the user, and sets the SecurityContext if authentication succeeds.
 *
 * Runs once per request before Spring's username/password authentication filter.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Processes each incoming request:
     * - Extracts JWT from cookie or Authorization header(fallback)
     * - Validates the token
     * - Loads user details and sets authentication in the SecurityContext
     * - Continues the filter chain regardless of authentication outcome
     *
     * If no valid token is found, the request proceeds as an unauthenticated user.
     */
    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        log.debug("---JwtAuthenticationFilter start---");

        String token = jwtUtils.extractJwtFromCookie(request);

        if (token == null){
            token = jwtUtils.extractJwtFromRequest(request); //fallback if it doesnt work with extractJwtFromCookie
        }

        if (token == null){
            log.debug("No JWT token found in cookie or Authorization header");
            filterChain.doFilter(request,response);
            log.debug("--JwtAuthenticationFilter end (no token)--");
            return;
        }

        if(!jwtUtils.validateJwtToken(token)){
            log.warn("Jwt validation failed");
            filterChain.doFilter(request,response);
            log.debug("--JwtAuthenticationFilter end (invalid token)--");
            return;
        }

        log.debug("JWT token validated successfully");

        String username = jwtUtils.getUsernameFromJwtToken(token);
        log.debug("Extracted username '{}' from JWT", username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            log.debug("SecurityContext updated for authenticated user '{}'", username);
        }

        filterChain.doFilter(request,response);

        log.debug("---JwtAuthenticationFilter END ---");

    }
}
