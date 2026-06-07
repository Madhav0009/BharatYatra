package com.Madhav.bharatYatra.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.Madhav.bharatYatra.service.OtpService;
import com.Madhav.bharatYatra.model.User;
import com.Madhav.bharatYatra.repository.UserRepository;



@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class ForgotPasswordController {

    @Autowired
    private OtpService otpService;
   
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // SEND OTP
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @RequestParam String email) {

    	Optional<User> optionalUser = 
    	        userRepository.findByEmail(email);

    	if (optionalUser.isEmpty()) {

    	    return ResponseEntity
    	            .badRequest()
    	            .body("Email not registered");
    	}

    	User user = optionalUser.get();

        otpService.sendOtp(email);

        return ResponseEntity.ok("OTP Sent Successfully");
    }

    // VERIFY OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {

        boolean valid =
                otpService.verifyOtp(email, otp);

        if (!valid) {

            return ResponseEntity
                    .badRequest()
                    .body("Invalid or Expired OTP");
        }

        return ResponseEntity.ok("OTP Verified");
    }

    // RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("User not found");
        }

        User user = optionalUser.get();

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        otpService.clearOtp(email);

        return ResponseEntity.ok(
                "Password Reset Successful"
        );
    }
}
