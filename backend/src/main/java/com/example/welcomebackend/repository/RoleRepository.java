package com.example.welcomebackend.repository;

import com.example.welcomebackend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByCompanyId(Long companyId);
    Optional<Role> findByNameAndCompanyId(String name, Long companyId);
}
