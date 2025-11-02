package com.example.welcomebackend.controller;

import com.example.welcomebackend.model.Gateway;
import com.example.welcomebackend.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gateways")
@CrossOrigin(origins = "http://localhost:4200")
public class GatewayController {

    @Autowired
    private GatewayService gatewayService;

    @PostMapping("/process/{processId}")
    public ResponseEntity<?> createGateway(@PathVariable Long processId, @RequestBody Gateway gateway) {
        try {
            Gateway created = gatewayService.createGateway(processId, gateway);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{gatewayId}")
    public ResponseEntity<?> updateGateway(@PathVariable Long gatewayId, @RequestBody Gateway gateway) {
        try {
            Gateway updated = gatewayService.updateGateway(gatewayId, gateway);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{gatewayId}")
    public ResponseEntity<?> deleteGateway(@PathVariable Long gatewayId) {
        try {
            gatewayService.deleteGateway(gatewayId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Gateway deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/process/{processId}")
    public ResponseEntity<List<Gateway>> getGatewaysByProcess(@PathVariable Long processId) {
        return ResponseEntity.ok(gatewayService.getGatewaysByProcess(processId));
    }

    @GetMapping("/{gatewayId}")
    public ResponseEntity<?> getGateway(@PathVariable Long gatewayId) {
        try {
            Gateway gateway = gatewayService.getGatewayById(gatewayId);
            return ResponseEntity.ok(gateway);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
