package com.example.welcomebackend.repository;

import com.example.welcomebackend.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByNit(String nit);
    boolean existsByNit(String nit);
}
