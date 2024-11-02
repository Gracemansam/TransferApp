package com.patientRecTransferApp.controller;

import com.patientRecTransferApp.dto.response.AuditLogResponse;
import com.patientRecTransferApp.serviceImpl.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit-log")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/history")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogHistory() {
        List<AuditLogResponse> auditLogs = auditLogService.getAuditLogsForCurrentUser();
        return ResponseEntity.ok(auditLogs);
    }
}
