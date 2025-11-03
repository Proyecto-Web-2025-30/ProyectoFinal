package com.example.welcomebackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(
        name = "gateways",
        indexes = { @Index(name = "idx_gateway_process", columnList = "process_id") }
)
public class Gateway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 120, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "gateway_type", length = 20, nullable = false)
    private GatewayType gatewayType;

    @Column(length = 2000)
    private String conditions;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_id")
    @JsonIgnore
    private Process process;

    // Canvas coordinates
    private Double x;
    private Double y;

    // getters & setters
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
    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }
    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }
}


