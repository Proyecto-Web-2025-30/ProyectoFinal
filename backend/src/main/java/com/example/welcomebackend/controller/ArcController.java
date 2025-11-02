package com.example.welcomebackend.controller;

import com.example.welcomebackend.model.Arc;
import com.example.welcomebackend.service.ArcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/arcs")
@CrossOrigin(origins = "http://localhost:4200")
public class ArcController {

    @Autowired
    private ArcService arcService;

    @PostMapping("/process/{processId}")
    public ResponseEntity<?> createArc(@PathVariable Long processId, @RequestBody Arc arc) {
        try {
            Arc created = arcService.createArc(processId, arc);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{arcId}")
    public ResponseEntity<?> updateArc(@PathVariable Long arcId, @RequestBody Arc arc) {
        try {
            Arc updated = arcService.updateArc(arcId, arc);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{arcId}")
    public ResponseEntity<?> deleteArc(@PathVariable Long arcId) {
        try {
            arcService.deleteArc(arcId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Arc deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/process/{processId}")
    public ResponseEntity<List<Arc>> getArcsByProcess(@PathVariable Long processId) {
        return ResponseEntity.ok(arcService.getArcsByProcess(processId));
    }

    @GetMapping("/{arcId}")
    public ResponseEntity<?> getArc(@PathVariable Long arcId) {
        try {
            Arc arc = arcService.getArcById(arcId);
            return ResponseEntity.ok(arc);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
