package com.example.welcomebackend.controller;

import com.example.welcomebackend.model.Activity;
import com.example.welcomebackend.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "http://localhost:4200")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping("/process/{processId}")
    public ResponseEntity<?> createActivity(
            @PathVariable Long processId,
            @RequestBody Map<String, Object> request) {
        try {
            Activity activity = new Activity();
            activity.setName((String) request.get("name"));
            activity.setActivityType((String) request.get("activityType"));
            activity.setDescription((String) request.get("description"));
            
            Long roleId = request.get("roleId") != null ? 
                    Long.valueOf(request.get("roleId").toString()) : null;
            
            Activity created = activityService.createActivity(processId, activity, roleId);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{activityId}")
    public ResponseEntity<?> updateActivity(
            @PathVariable Long activityId,
            @RequestBody Map<String, Object> request) {
        try {
            Activity activity = new Activity();
            activity.setName((String) request.get("name"));
            activity.setActivityType((String) request.get("activityType"));
            activity.setDescription((String) request.get("description"));
            
            Long roleId = request.get("roleId") != null ? 
                    Long.valueOf(request.get("roleId").toString()) : null;
            
            Activity updated = activityService.updateActivity(activityId, activity, roleId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{activityId}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long activityId) {
        try {
            activityService.deleteActivity(activityId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Activity deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/process/{processId}")
    public ResponseEntity<List<Activity>> getActivitiesByProcess(@PathVariable Long processId) {
        return ResponseEntity.ok(activityService.getActivitiesByProcess(processId));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<?> getActivity(@PathVariable Long activityId) {
        try {
            Activity activity = activityService.getActivityById(activityId);
            return ResponseEntity.ok(activity);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
