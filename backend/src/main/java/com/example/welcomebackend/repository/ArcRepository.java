package com.example.welcomebackend.repository;

import com.example.welcomebackend.model.Arc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArcRepository extends JpaRepository<Arc, Long> {
    List<Arc> findByProcessId(Long processId);
}
