package com.example.welcomebackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(
        name = "arcs",
        indexes = { @Index(name = "idx_arc_process", columnList = "process_id") }
)
public class Arc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "ACTIVITY" o "GATEWAY"
    @Column(length = 20, nullable = false)
    private String sourceType;

    private Long sourceId;

    // "ACTIVITY" o "GATEWAY"
    @Column(length = 20, nullable = false)
    private String targetType;

    private Long targetId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_id")
    @JsonIgnore
    private Process process;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public Process getProcess() { return process; }
    public void setProcess(Process process) { this.process = process; }
}
