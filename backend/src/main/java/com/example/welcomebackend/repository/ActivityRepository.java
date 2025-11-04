package com.example.welcomebackend.repository;

import com.example.welcomebackend.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByProcessId(Long processId);

    long countByResponsibleRole_Id(Long roleId); //  para borrar roles con seguridad
}
