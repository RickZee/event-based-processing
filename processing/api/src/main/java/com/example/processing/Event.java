package com.example.processing;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID eventTypeId;
    private String eventTypeName;
    private ZonedDateTime tt;
    private ZonedDateTime vt;
    private UUID schemaVersionId;
    private String schemaVersionName;
    private String producerName;
    private String userId;

    public Event() {}

    public Event(UUID eventTypeId, String eventTypeName, ZonedDateTime tt, ZonedDateTime vt, UUID schemaVersionId, String schemaVersionName, String producerName, String userId) {
        this.eventTypeId = eventTypeId;
        this.eventTypeName = eventTypeName;
        this.tt = tt;
        this.vt = vt;
        this.schemaVersionId = schemaVersionId;
        this.schemaVersionName = schemaVersionName;
        this.producerName = producerName;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public UUID getEventTypeId() { return eventTypeId; }
    public void setEventTypeId(UUID eventTypeId) { this.eventTypeId = eventTypeId; }
    public String getEventTypeName() { return eventTypeName; }
    public void setEventTypeName(String eventTypeName) { this.eventTypeName = eventTypeName; }
    public ZonedDateTime getTt() { return tt; }
    public void setTt(ZonedDateTime tt) { this.tt = tt; }
    public ZonedDateTime getVt() { return vt; }
    public void setVt(ZonedDateTime vt) { this.vt = vt; }
    public UUID getSchemaVersionId() { return schemaVersionId; }
    public void setSchemaVersionId(UUID schemaVersionId) { this.schemaVersionId = schemaVersionId; }
    public String getSchemaVersionName() { return schemaVersionName; }
    public void setSchemaVersionName(String schemaVersionName) { this.schemaVersionName = schemaVersionName; }
    public String getProducerName() { return producerName; }
    public void setProducerName(String producerName) { this.producerName = producerName; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
