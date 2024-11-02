package com.patientRecTransferApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogResponse {
    private String dateAndTime;
    private String description;
    private String facilityName;


}
