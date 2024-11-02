package com.patientRecTransferApp.serviceImpl;

import com.patientRecTransferApp.dto.response.AuditLogResponse;
import com.patientRecTransferApp.entity.AuditLog;
import com.patientRecTransferApp.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditRepository auditLogRepository;

    @Autowired
    private AppUserServiceImpl appUserService;

    @Autowired
    private DataTransferService dataTransferService;

    public List<AuditLogResponse> getAuditLogsForCurrentUser() {
        Long hospitalId = dataTransferService.getHospitalId().getHosiptalId();
        List<AuditLog> auditLogs = auditLogRepository.findByFacilityId(hospitalId);

        List<AuditLogResponse> responses = new ArrayList<>();
        for (AuditLog log : auditLogs) {
            AuditLogResponse response = new AuditLogResponse();
            response.setDateAndTime(log.getTimestamp().toString());

            if (log.getOperation().equals("Encryption")) {
                if (log.getRecipientFacilityId().equals(hospitalId)) {
                    response.setDescription("Received an encrypted file");
                    response.setFacilityName(appUserService.getFacilityNameById(log.getSenderFacilityId()));
                } else if (log.getSenderFacilityId().equals(hospitalId)) {
                    response.setDescription("You sent and encrypted file");
                    response.setFacilityName(appUserService.getFacilityNameById(log.getRecipientFacilityId()));
                }
            } else if (log.getOperation().equals("Decryption") && log.getRecipientFacilityId().equals(hospitalId)) {
                response.setDescription("You decrypted the file from " + appUserService.getFacilityNameById(log.getSenderFacilityId()));
                response.setFacilityName(appUserService.getFacilityNameById(log.getSenderFacilityId()));
            }

            responses.add(response);
        }

        return responses;
    }
}
