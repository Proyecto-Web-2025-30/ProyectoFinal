package com.example.welcomebackend.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:4200"  })
public class WelcomeController {

    @GetMapping("/welcome")
    public Map<String, String> getWelcomeMessage() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Spring Boot and Angular!");
        response.put("status", "success");
        return response;
    }
}
