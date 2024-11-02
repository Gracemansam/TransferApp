package com.patientRecTransferApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class DataTransferResponses {
    private String description;

    private LocalDateTime dateTime;
    private Long requestId;
    private Long requestingFacility;
    private Long recipientFacility;

    // Getters and Setters
}
