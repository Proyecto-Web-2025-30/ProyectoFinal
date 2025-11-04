package com.example.welcomebackend.controller;

import com.example.welcomebackend.dto.RegisterCompanyRequest;
import com.example.welcomebackend.model.Company;
import com.example.welcomebackend.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = { "http://localhost:4200" })
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/register")
    public ResponseEntity<?> registerCompany(@Valid @RequestBody RegisterCompanyRequest request) {
        try {
            Company company = companyService.registerCompany(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Company registered successfully");
            response.put("companyId", company.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
