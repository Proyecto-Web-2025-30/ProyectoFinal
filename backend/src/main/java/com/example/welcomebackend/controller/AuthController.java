package com.example.welcomebackend.controller;

import com.example.welcomebackend.dto.LoginRequest;
import com.example.welcomebackend.model.AppUser;
import com.example.welcomebackend.repository.AppUserRepository;
import com.example.welcomebackend.config.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost:4200" })
public class AuthController {

    @Autowired private AppUserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenService jwtTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.getUsername()).orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            return ResponseEntity.status(401).body(error);
        }

        String token = jwtTokenService.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("tokenType", "Bearer");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("fullName", user.getFullName());
        response.put("companyId", user.getCompany() != null ? user.getCompany().getId() : null);
        response.put("companyName", user.getCompany() != null ? user.getCompany().getName() : null);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para renovar el token JWT.
     *
     * Espera el token (posiblemente expirado) en el header Authorization: Bearer xxx.
     * Valida la firma, obtiene el usuario y genera un nuevo token con nueva expiración.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Missing or invalid Authorization header");
            return ResponseEntity.status(401).body(error);
        }

        String token = authHeader.substring(7);
        String username;
        try {
            username = jwtTokenService.extractUsernameAllowExpired(token);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid token");
            return ResponseEntity.status(401).body(error);
        }

        AppUser user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(401).body(error);
        }

        String newToken = jwtTokenService.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("token", newToken);
        response.put("tokenType", "Bearer");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());

        return ResponseEntity.ok(response);
    }

    /**
     * Devuelve la información del usuario autenticado usando el JWT enviado.
     * Este endpoint requiere un token válido en Authorization: Bearer xxx.
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            return ResponseEntity.status(401).body(error);
        }

        AppUser user = userRepository.findByUsername(principal.getUsername()).orElse(null);
        if (user == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(404).body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("fullName", user.getFullName());
        response.put("companyId", user.getCompany() != null ? user.getCompany().getId() : null);
        response.put("companyName", user.getCompany() != null ? user.getCompany().getName() : null);

        return ResponseEntity.ok(response);
    }
}

