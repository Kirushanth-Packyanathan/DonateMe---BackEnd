package com.example.demo.service;

import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {


    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    public RefreshToken createRefreshToken(String email){
        User user = userRepository.findByEmail(email) // findByEmail was findByUsername
                .orElseThrow(
                        () -> new UsernameNotFoundException("user not found with email " + email));
        RefreshToken refreshToken = user.getRefreshToken();
        if (refreshToken == null){
            refreshToken=RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(5*60*60*1000))
                    .user(user)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken){
        RefreshToken refreshToken1=refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new RuntimeException("Refresh token not found"));

        if (refreshToken1.getExpirationTime().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(refreshToken1);
            throw new RuntimeException("refresh token expired");
        }
        return refreshToken1;
    }
}
