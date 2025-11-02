package com.example.welcomebackend.service;

import com.example.welcomebackend.model.Company;
import com.example.welcomebackend.model.Role;
import com.example.welcomebackend.repository.ActivityRepository;
import com.example.welcomebackend.repository.CompanyRepository;
import com.example.welcomebackend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Transactional
    public Role createRole(Long companyId, Role role) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        role.setCompany(company);
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Long roleId, Role updatedRole) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        role.setName(updatedRole.getName());
        role.setDescription(updatedRole.getDescription());
        
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        // Check if role is assigned to any activity
        long count = activityRepository.findAll().stream()
                .filter(a -> a.getResponsibleRole() != null && a.getResponsibleRole().getId().equals(roleId))
                .count();
        
        if (count > 0) {
            throw new RuntimeException("Cannot delete role that is assigned to activities");
        }
        
        roleRepository.delete(role);
    }

    public List<Role> getRolesByCompany(Long companyId) {
        return roleRepository.findByCompanyId(companyId);
    }

    public Role getRoleById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }
}
