package com.example.welcomebackend.controller;

import com.example.welcomebackend.model.Process;
import com.example.welcomebackend.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/processes")
@CrossOrigin(origins = "http://localhost:4200")
public class ProcessController {

    @Autowired
    private ProcessService processService;

    @PostMapping("/company/{companyId}")
    public ResponseEntity<?> createProcess(@PathVariable Long companyId, @RequestBody Process process) {
        try {
            Process created = processService.createProcess(companyId, process);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Process created successfully");
            response.put("id", created.getId());
            response.put("name", created.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            error.put("details", "Failed to create process. Check that all fields are filled correctly.");
            e.printStackTrace(); // Log the full error
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{processId}")
    public ResponseEntity<?> updateProcess(@PathVariable Long processId, @RequestBody Process process) {
        try {
            Process updated = processService.updateProcess(processId, process);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{processId}")
    public ResponseEntity<?> deleteProcess(@PathVariable Long processId) {
        try {
            processService.deleteProcess(processId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Process deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{processId}")
    public ResponseEntity<?> getProcess(@PathVariable Long processId) {
        try {
            Process process = processService.getProcessById(processId);
            return ResponseEntity.ok(process);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Process>> getProcessesByCompany(
            @PathVariable Long companyId,
            @RequestParam(required = false) String category) {
        
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(processService.getProcessesByCompanyAndCategory(companyId, category));
        }
        return ResponseEntity.ok(processService.getProcessesByCompany(companyId));
    }
}
