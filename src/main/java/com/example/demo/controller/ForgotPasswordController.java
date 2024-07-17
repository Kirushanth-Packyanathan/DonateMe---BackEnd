package com.example.demo.controller;

import com.example.demo.DTO.ChangePassword;
import com.example.demo.DTO.MailBody;
import com.example.demo.entity.ForgotPassword;
import com.example.demo.entity.User;
import com.example.demo.repository.ForgotPasswordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RequiredArgsConstructor
@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/verifyEmail/{email}")
    private ResponseEntity<String> verifyEmail(@PathVariable String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Please Provide an valid email"));

        int otp=otpGenerator();

        MailBody mailBody= MailBody.builder()
                .to(email)
                .text("This is the OTP for your forgot password request :"+otp)
                .subject("OTP for forgot password request")
                .build();

        ForgotPassword fp= ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis()+70*1000))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent for verification");

    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp,@PathVariable String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Please Provide an valid email"));
        ForgotPassword fp= forgotPasswordRepository.findByOtpAndUser(otp,user).orElseThrow(()->new UsernameNotFoundException("Please provide an valid email"));

        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP has expired", HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("OTP verified!");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,@PathVariable String email){
        if (!Objects.equals(changePassword.password(),changePassword.repeatPassword())){
            return new ResponseEntity<>("please enter the pasword again password not matching",HttpStatus.EXPECTATION_FAILED);
        }
        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email,encodedPassword);

        return ResponseEntity.ok("password changed successfully");

    }

    private Integer otpGenerator(){
        Random random=new Random();
        return random.nextInt(100_000,999_999);
    }



}
