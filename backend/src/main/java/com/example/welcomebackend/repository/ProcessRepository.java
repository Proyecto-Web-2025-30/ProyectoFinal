package com.example.welcomebackend.repository;

import com.example.welcomebackend.model.Process;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessRepository extends JpaRepository<Process, Long> {
    List<Process> findByCompanyIdAndActiveTrue(Long companyId);
    List<Process> findByCompanyIdAndCategoryAndActiveTrue(Long companyId, String category);
}
