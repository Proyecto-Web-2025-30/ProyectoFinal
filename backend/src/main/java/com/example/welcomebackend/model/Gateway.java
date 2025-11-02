package com.example.welcomebackend.model;

import jakarta.persistence.*;

@Entity
public class Gateway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private GatewayType gatewayType;

    @Column(length = 2000)
    private String conditions;

    @ManyToOne(fetch = FetchType.LAZY)
    private Process process;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public GatewayType getGatewayType() { return gatewayType; }
    public void setGatewayType(GatewayType gatewayType) { this.gatewayType = gatewayType; }
    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }
    public Process getProcess() { return process; }
    public void setProcess(Process process) { this.process = process; }
}

enum GatewayType {
    EXCLUSIVE, PARALLEL, INCLUSIVE
}
