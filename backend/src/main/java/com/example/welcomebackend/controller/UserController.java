package com.example.welcomebackend.controller;

import com.example.welcomebackend.dto.CreateUserRequest;
import com.example.welcomebackend.model.AppUser;
import com.example.welcomebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/company/{companyId}")
    public ResponseEntity<?> createUser(@PathVariable Long companyId, @RequestBody CreateUserRequest request) {
        try {
            AppUser user = userService.createUser(companyId, request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully");
            response.put("userId", user.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<AppUser>> getUsersByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(userService.getUsersByCompany(companyId));
    }
}
