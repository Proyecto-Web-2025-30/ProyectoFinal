package com.example.welcomebackend.service;

import com.example.welcomebackend.dto.CreateUserRequest;
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

import java.util.List;

@Service
public class UserService {

    @Autowired private AppUserRepository userRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public AppUser createUser(Long companyId, CreateUserRequest request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepository.findByNameAndCompanyId(request.getUserRole(), companyId)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(request.getUserRole());
                    newRole.setDescription(request.getUserRole() + " role");
                    newRole.setCompany(company);
                    return roleRepository.save(newRole);
                });

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setCompany(company);
        user.getRoles().add(role);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<AppUser> getUsersByCompany(Long companyId) {
        return userRepository.findByCompany_Id(companyId);
    }
}
