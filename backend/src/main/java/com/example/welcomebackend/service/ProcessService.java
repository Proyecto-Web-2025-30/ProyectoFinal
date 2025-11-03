package com.example.welcomebackend.service;

import com.example.welcomebackend.model.Company;
import com.example.welcomebackend.model.Process;
import com.example.welcomebackend.repository.CompanyRepository;
import com.example.welcomebackend.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProcessService {

    @Autowired private ProcessRepository processRepository;
    @Autowired private CompanyRepository companyRepository;

    @Transactional
    public Process createProcess(Long companyId, Process process) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        process.setCompany(company);
        process.setCreatedAt(LocalDateTime.now());
        process.setUpdatedAt(LocalDateTime.now());
        process.setActive(true);

        return processRepository.save(process);
    }

    @Transactional
    public Process updateProcess(Long processId, Process updatedProcess) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found"));

        process.setName(updatedProcess.getName());
        process.setDescription(updatedProcess.getDescription());
        process.setCategory(updatedProcess.getCategory());
        process.setStatus(updatedProcess.getStatus());
        process.setUpdatedAt(LocalDateTime.now());

        return processRepository.save(process);
    }

    @Transactional
    public void deleteProcess(Long processId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found"));

        process.setActive(false);
        processRepository.save(process);
    }

    @Transactional(readOnly = true)
    public Process getProcessById(Long processId) {
        return processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found"));
    }

    @Transactional(readOnly = true)
    public List<Process> getProcessesByCompany(Long companyId) {
        return processRepository.findByCompanyIdAndActiveTrue(companyId);
    }

    @Transactional(readOnly = true)
    public List<Process> getProcessesByCompanyAndCategory(Long companyId, String category) {
        return processRepository.findByCompanyIdAndCategoryAndActiveTrue(companyId, category);
    }
}
