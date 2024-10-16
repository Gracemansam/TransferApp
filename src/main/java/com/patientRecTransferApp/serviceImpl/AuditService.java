package com.patientRecTransferApp.serviceImpl;

import com.patientRecTransferApp.entity.AuditLog;
import com.patientRecTransferApp.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    @Autowired
    private AuditRepository auditRepository;

    public void logEncryption(Long senderFacilityId, Long recipientFacilityId) {
        AuditLog log = new AuditLog();
        log.setOperation("Encryption");
        log.setSenderFacilityId(senderFacilityId);
        log.setRecipientFacilityId(recipientFacilityId);
        log.setTimestamp(LocalDateTime.now());
        auditRepository.save(log);
    }

    public void logDecryption(Long senderFacilityId, Long recipientFacilityId) {
        AuditLog log = new AuditLog();
        log.setOperation("Decryption");
        log.setSenderFacilityId(senderFacilityId);
        log.setRecipientFacilityId(recipientFacilityId);
        log.setTimestamp(LocalDateTime.now());
        auditRepository.save(log);
    }
}
