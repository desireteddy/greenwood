package com.smartcampus.service;

import com.smartcampus.model.AuditLog;
import com.smartcampus.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void record(String actor, String action, String entityType, String entityId) {
        repository.save(new AuditLog(actor, action, entityType, entityId));
    }
}
