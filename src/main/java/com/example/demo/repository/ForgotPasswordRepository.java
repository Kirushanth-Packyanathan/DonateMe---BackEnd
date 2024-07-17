package com.example.demo.repository;

import com.example.demo.entity.ForgotPassword;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {
    @Query("SELECT fp from ForgotPassword fp where fp.otp=?1 and fp.otp=?2")
    Optional<ForgotPassword> findByOtpAndUser (Integer otp, User user);
}
