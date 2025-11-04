package com.example.welcomebackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(
        name = "activities",
        indexes = {
                @Index(name = "idx_activity_process", columnList = "process_id"),
                @Index(name = "idx_activity_resp_role", columnList = "responsible_role_id")
        }
)
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 120, nullable = false)
    private String name;

    @Column(length = 60)
    private String activityType;

    @Column(length = 2000)
    private String description;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "responsible_role_id")
    private Role responsibleRole;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_id")
    private Process process;

    // Canvas coordinates
    private Double x;
    private Double y;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Role getResponsibleRole() { return responsibleRole; }
    public void setResponsibleRole(Role responsibleRole) { this.responsibleRole = responsibleRole; }
    public Process getProcess() { return process; }
    public void setProcess(Process process) { this.process = process; }
    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }
    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }
}
