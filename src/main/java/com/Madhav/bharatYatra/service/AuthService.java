package com.Madhav.bharatYatra.service;

import com.Madhav.bharatYatra.config.JwtUtils;
import com.Madhav.bharatYatra.dto.AuthResponse;
import com.Madhav.bharatYatra.dto.LoginRequest;
import com.Madhav.bharatYatra.dto.RegisterRequest;
import com.Madhav.bharatYatra.model.User;
import com.Madhav.bharatYatra.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
 

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    public AuthResponse register(RegisterRequest req) {

        if (userRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already registered: " + req.getEmail()
            );
        }

        User user = User.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .city(req.getCity())
                .role(User.Role.USER)
                .build();

        user = userRepo.save(user);

        String token = jwtUtils.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest req) {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        // SAFE PASSWORD CHECK (NO SPRING SECURITY CRASH)
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // update location if present
        if (req.getLatitude() != null && req.getLongitude() != null) {
            user.setLatitude(req.getLatitude());
            user.setLongitude(req.getLongitude());
            userRepo.save(user);
        }

        String token = jwtUtils.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}