package com.example.welcomebackend.service;

import com.example.welcomebackend.dto.RegisterCompanyRequest;
import com.example.welcomebackend.model.AppUser;
import com.example.welcomebackend.model.Company;
import com.example.welcomebackend.model.Role;
import com.example.welcomebackend.repository.AppUserRepository;
import com.example.welcomebackend.repository.CompanyRepository;
import com.example.welcomebackend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

    @Autowired private CompanyRepository companyRepository;
    @Autowired private AppUserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public Company registerCompany(RegisterCompanyRequest request) {
        if (companyRepository.existsByNit(request.getNit())) {
            throw new RuntimeException("Company with NIT already exists");
        }
        if (userRepository.existsByUsername(request.getAdminUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getAdminEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setNit(request.getNit());
        company.setContactEmail(request.getContactEmail());
        company = companyRepository.save(company);

        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setDescription("Administrator role");
        adminRole.setCompany(company);
        adminRole = roleRepository.save(adminRole);

        AppUser admin = new AppUser();
        admin.setUsername(request.getAdminUsername());
        admin.setEmail(request.getAdminEmail());
        admin.setPassword(passwordEncoder.encode(request.getAdminPassword()));
        admin.setFullName(request.getAdminFullName());
        admin.setCompany(company);
        admin.getRoles().add(adminRole);
        userRepository.save(admin);

        return company;
    }
}
