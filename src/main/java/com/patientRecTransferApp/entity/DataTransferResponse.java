package com.patientRecTransferApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class DataTransferResponse {
    private String message;
    private String status;
    private Long requestId;

}