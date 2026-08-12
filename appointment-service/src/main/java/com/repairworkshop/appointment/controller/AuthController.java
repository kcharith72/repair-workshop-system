package com.repairworkshop.appointment.controller;

import com.repairworkshop.appointment.dto.AuthDTOs;
import com.repairworkshop.appointment.entity.User;
import com.repairworkshop.appointment.repository.UserRepository;
import com.repairworkshop.appointment.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthDTOs.AuthResponse> login(
            @RequestBody AuthDTOs.LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthDTOs.AuthResponse(null, null, null, "Invalid credentials"));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken(userDetails, user.getRole());

        return ResponseEntity.ok(
            new AuthDTOs.AuthResponse(token, user.getUsername(), user.getRole(),
                "Login successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDTOs.AuthResponse> register(
            @RequestBody AuthDTOs.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new AuthDTOs.AuthResponse(null, null, null, "Username already exists"));
        }

        String role = (request.getRole() != null &&
                request.getRole().equalsIgnoreCase("ADMIN"))
                ? "ROLE_ADMIN" : "ROLE_USER";

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthDTOs.AuthResponse(null, user.getUsername(), role,
                        "User registered successfully"));
    }
}
