package com.amanda.weather_app_auth.controller;

import com.amanda.weather_app_auth.dto.CustomUserCreationDTO;
import com.amanda.weather_app_auth.dto.CustomUserLoginDTO;
import com.amanda.weather_app_auth.dto.CustomUserLoginResponseDTO;
import com.amanda.weather_app_auth.dto.CustomUserResponseDTO;
import com.amanda.weather_app_auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomUserResponseDTO> register(@RequestBody @Valid CustomUserCreationDTO dto){
        CustomUserResponseDTO customUserResponseDTO = authService.register(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(customUserResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<CustomUserLoginResponseDTO> login(@RequestBody @Valid CustomUserLoginDTO dto){
        CustomUserLoginResponseDTO responseDTO = authService.login(dto);
        String token = responseDTO.token();

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)  //ändra till true innan prod
                .sameSite("Lax") //ändra ev till none innan prod
                .path("/")
                .maxAge(24*60*60) //Ändra ev innan prod.
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(responseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out successfully");

    }


}
