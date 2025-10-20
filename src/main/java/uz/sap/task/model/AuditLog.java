package uz.sap.task.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "audit_log")
public class AuditLog {
    @Id
    @GeneratedValue
    private Long id;
    private String processInstanceId;
    private String eventType;
    @Column(columnDefinition="text")
    private String payload;
    private Instant createdAt = Instant.now();

    // constructors, getters/setters

    public AuditLog(String processInstanceId, String eventType, String payload) {
        this.processInstanceId = processInstanceId;
        this.eventType = eventType;
        this.payload = payload;
    }

    public AuditLog() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

