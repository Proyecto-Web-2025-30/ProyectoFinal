package com.example.welcomebackend.service;

import com.example.welcomebackend.model.Activity;
import com.example.welcomebackend.model.Process;
import com.example.welcomebackend.model.Role;
import com.example.welcomebackend.repository.ActivityRepository;
import com.example.welcomebackend.repository.ProcessRepository;
import com.example.welcomebackend.repository.ArcRepository;
import com.example.welcomebackend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ArcRepository arcRepository;

    @Transactional
    public Activity createActivity(Long processId, Activity activity, Long roleId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found"));
        
        activity.setProcess(process);
        
        if (roleId != null) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            activity.setResponsibleRole(role);
        }
        
        process.setUpdatedAt(LocalDateTime.now());
        processRepository.save(process);
        
        return activityRepository.save(activity);
    }

    @Transactional
    public Activity updateActivity(Long activityId, Activity updatedActivity, Long roleId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        
        activity.setName(updatedActivity.getName());
        activity.setActivityType(updatedActivity.getActivityType());
        activity.setDescription(updatedActivity.getDescription());
        activity.setX(updatedActivity.getX());
        activity.setY(updatedActivity.getY());
        
        if (roleId != null) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            activity.setResponsibleRole(role);
        }
        
        Process process = activity.getProcess();
        process.setUpdatedAt(LocalDateTime.now());
        processRepository.save(process);
        
        return activityRepository.save(activity);
    }

    @Transactional
    public void deleteActivity(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        
        Process process = activity.getProcess();
        process.setUpdatedAt(LocalDateTime.now());
        processRepository.save(process);
        
        // Delete arcs that reference this activity as source or target
        arcRepository.deleteByProcessIdAndSourceTypeAndSourceId(process.getId(), "ACTIVITY", activityId);
        arcRepository.deleteByProcessIdAndTargetTypeAndTargetId(process.getId(), "ACTIVITY", activityId);

        activityRepository.delete(activity);
    }

    public List<Activity> getActivitiesByProcess(Long processId) {
        return activityRepository.findByProcessId(processId);
    }

    public Activity getActivityById(Long activityId) {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
    }
}
