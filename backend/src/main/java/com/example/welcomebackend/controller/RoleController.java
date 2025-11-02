package com.example.welcomebackend.controller;

import com.example.welcomebackend.model.Role;
import com.example.welcomebackend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:4200")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/company/{companyId}")
    public ResponseEntity<?> createRole(@PathVariable Long companyId, @RequestBody Role role) {
        try {
            Role created = roleService.createRole(companyId, role);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Role created successfully");
            response.put("id", created.getId());
            response.put("name", created.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            error.put("details", "Failed to create role. Make sure name and description are provided.");
            e.printStackTrace(); // Log the full error
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<?> updateRole(@PathVariable Long roleId, @RequestBody Role role) {
        try {
            Role updated = roleService.updateRole(roleId, role);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable Long roleId) {
        try {
            roleService.deleteRole(roleId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Role deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Role>> getRolesByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(roleService.getRolesByCompany(companyId));
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<?> getRole(@PathVariable Long roleId) {
        try {
            Role role = roleService.getRoleById(roleId);
            return ResponseEntity.ok(role);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
