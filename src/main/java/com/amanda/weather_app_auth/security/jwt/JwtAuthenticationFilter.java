package com.amanda.weather_app_auth.security.jwt;

import com.amanda.weather_app_auth.user.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("---JwtAuthenticationFilter start---");

        String token = jwtUtils.extractJwtFromCookie(request);

        if (token == null){
            token = jwtUtils.extractJwtFromRequest(request); //om inte det funkar med extractJwtFromCookie
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
