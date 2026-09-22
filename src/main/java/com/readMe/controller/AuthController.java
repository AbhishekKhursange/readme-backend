package com.readMe.controller;

import com.readMe.config.JwtService;
import com.readMe.config.RefreshTokenService;
import com.readMe.dto.LoginRequest;
import com.readMe.dto.LoginResponse;
import com.readMe.dto.RefreshRequest;
import com.readMe.dto.RegisterRequest;
import com.readMe.dto.TokenResponse;
import com.readMe.dto.UserResponse;
import com.readMe.entity.User;
import com.readMe.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "An account with this email already exists."));
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFavoriteGenre(request.getFavoriteGenre());
        userRepository.save(user);

        return ResponseEntity.ok(toResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            		.body(Map.of("message", "Invalid email or password."));
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.isAdmin());
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken, toResponse(user)));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequest request) {
        String email = refreshTokenService.getEmailForToken(request.getRefreshToken());

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Session expired. Please log in again."));
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            refreshTokenService.deleteToken(request.getRefreshToken());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Account no longer exists."));
        }

        String newAccessToken = jwtService.generateAccessToken(user.getEmail(), user.isAdmin());
        String newRefreshToken = refreshTokenService.rotateToken(request.getRefreshToken(), user.getEmail());

        return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        refreshTokenService.deleteToken(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getFavoriteGenre(), user.isAdmin());
    }
}
