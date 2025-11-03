package com.example.welcomebackend.repository;

import com.example.welcomebackend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<AppUser> findByCompany_Id(Long companyId); // Consulta por empresa directamente en la bd
}
