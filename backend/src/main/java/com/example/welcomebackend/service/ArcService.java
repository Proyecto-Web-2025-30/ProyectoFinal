package com.example.welcomebackend.service;

import com.example.welcomebackend.model.Arc;
import com.example.welcomebackend.model.Process;
import com.example.welcomebackend.repository.ArcRepository;
import com.example.welcomebackend.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArcService {

    @Autowired
    private ArcRepository arcRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Transactional
    public Arc createArc(Long processId, Arc arc) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found"));
        
        arc.setProcess(process);
        
        process.setUpdatedAt(LocalDateTime.now());
        processRepository.save(process);
        
        return arcRepository.save(arc);
    }

    @Transactional
    public Arc updateArc(Long arcId, Arc updatedArc) {
        Arc arc = arcRepository.findById(arcId)
                .orElseThrow(() -> new RuntimeException("Arc not found"));
        
        arc.setSourceType(updatedArc.getSourceType());
        arc.setSourceId(updatedArc.getSourceId());
        arc.setTargetType(updatedArc.getTargetType());
        arc.setTargetId(updatedArc.getTargetId());
        
        Process process = arc.getProcess();
        process.setUpdatedAt(LocalDateTime.now());
        processRepository.save(process);
        
        return arcRepository.save(arc);
    }

    @Transactional
    public void deleteArc(Long arcId) {
        Arc arc = arcRepository.findById(arcId)
                .orElseThrow(() -> new RuntimeException("Arc not found"));
        
        Process process = arc.getProcess();
        process.setUpdatedAt(LocalDateTime.now());
        processRepository.save(process);
        
        arcRepository.delete(arc);
    }

    public List<Arc> getArcsByProcess(Long processId) {
        return arcRepository.findByProcessId(processId);
    }

    public Arc getArcById(Long arcId) {
        return arcRepository.findById(arcId)
                .orElseThrow(() -> new RuntimeException("Arc not found"));
    }
}
