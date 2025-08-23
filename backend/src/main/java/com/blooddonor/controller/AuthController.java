package com.blooddonor.controller;

import com.blooddonor.dto.UserRegistrationDto;
import com.blooddonor.dto.AuthRequest;
import com.blooddonor.model.User;
import com.blooddonor.repository.UserRepository;
import com.blooddonor.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            // Validate input
            if (registrationDto.getUsername() == null || registrationDto.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Username is required"));
            }

            if (registrationDto.getEmail() == null || registrationDto.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email is required"));
            }

            if (registrationDto.getPassword() == null || registrationDto.getPassword().length() < 6) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Password must be at least 6 characters"));
            }

            // Check if username already exists
            if (userRepository.existsByUsername(registrationDto.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Username is already taken!"));
            }

            // Check if email already exists
            if (userRepository.existsByEmail(registrationDto.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email is already in use!"));
            }

            // Create new user
            User user = new User();
            user.setUsername(registrationDto.getUsername().trim());
            user.setEmail(registrationDto.getEmail().trim());
            user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
            
            // Set role safely
            String role = registrationDto.getRole();
            if (role == null || role.trim().isEmpty()) {
                role = "ROLE_USER";
            } else if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role.toUpperCase();
            }
            user.setRole(role);
            user.setCreatedAt(new Date());

            User savedUser = userRepository.save(user);

            // Generate JWT
            String jwt = jwtService.generateToken(savedUser.getUsername());

            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "username", savedUser.getUsername(),
                    "role", savedUser.getRole(),
                    "message", "User registered successfully!"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody AuthRequest authRequest) {
        try {
            // Validate input
            if (authRequest.getUsername() == null || authRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Username is required"));
            }

            if (authRequest.getPassword() == null || authRequest.getPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Password is required"));
            }

            // Check if user exists
            Optional<User> userOptional = userRepository.findByUsername(authRequest.getUsername().trim());
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "User not found!"));
            }

            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername().trim(),
                            authRequest.getPassword()
                    )
            );

            User user = userOptional.get();

            // Generate JWT
            String jwt = jwtService.generateToken(user.getUsername());

            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "username", user.getUsername(),
                    "role", user.getRole(),
                    "message", "Login successful!"
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid username or password!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Login failed: " + e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                String username = jwtService.extractUsername(jwt);
                
                if (username != null && jwtService.validateToken(jwt)) {
                    Optional<User> userOptional = userRepository.findByUsername(username);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        return ResponseEntity.ok(Map.of(
                                "valid", true,
                                "username", user.getUsername(),
                                "role", user.getRole()
                        ));
                    }
                }
            }
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "Invalid token"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "Token validation failed"));
        }
    }
}