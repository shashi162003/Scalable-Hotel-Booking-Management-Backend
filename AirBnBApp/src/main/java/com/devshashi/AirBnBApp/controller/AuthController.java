package com.devshashi.AirBnBApp.controller;

import com.devshashi.AirBnBApp.dto.LoginDTO;
import com.devshashi.AirBnBApp.dto.LoginResponseDTO;
import com.devshashi.AirBnBApp.dto.SignUpRequestDTO;
import com.devshashi.AirBnBApp.dto.UserDTO;
import com.devshashi.AirBnBApp.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody SignUpRequestDTO signUpRequestDTO){
        return new ResponseEntity<>(authService.signUp(signUpRequestDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response){
        String[] tokens = authService.login(loginDTO);
        Cookie cookie = new Cookie("refreshToken", tokens[1]);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDTO(tokens[0]));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(cookie -> cookie.getValue())
                .orElseThrow(() -> new AuthenticationServiceException("Refresh Token not found inside cookies"));
        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDTO(accessToken));
    }
}
