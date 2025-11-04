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
@CrossOrigin(origins = { "http://localhost:4200" })
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
        } catch (RuntimeException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", ex.getMessage());
            error.put("details", "Failed to create process. Check fields.");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{processId}")
    public ResponseEntity<?> updateProcess(@PathVariable Long processId, @RequestBody Process process) {
        try {
            return ResponseEntity.ok(processService.updateProcess(processId, process));
        } catch (RuntimeException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
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
        } catch (RuntimeException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{processId}")
    public ResponseEntity<?> getProcess(@PathVariable Long processId) {
        try {
            return ResponseEntity.ok(processService.getProcessById(processId));
        } catch (RuntimeException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Process>> getProcessesByCompany(@PathVariable Long companyId,
                                                               @RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(processService.getProcessesByCompanyAndCategory(companyId, category));
        }
        return ResponseEntity.ok(processService.getProcessesByCompany(companyId));
    }
}
