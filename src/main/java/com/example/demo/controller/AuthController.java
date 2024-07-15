package com.example.demo.controller;

import com.example.demo.DTO.AuthResponse;
import com.example.demo.DTO.LogInRequest;
import com.example.demo.DTO.RefreshTokenRequest;
import com.example.demo.DTO.RegisterRequest;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.JwtService;
import com.example.demo.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/register/registerBenficier")
    ResponseEntity<AuthResponse> registerBenficier(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.registerBenficier(registerRequest));
    }

    @PostMapping("/logIn")
    ResponseEntity<AuthResponse> login(@RequestBody LogInRequest logInRequest) {
        return ResponseEntity.ok(authService.logIn(logInRequest));
    }

    @PostMapping("/refresh")
    ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build());

    }

}
