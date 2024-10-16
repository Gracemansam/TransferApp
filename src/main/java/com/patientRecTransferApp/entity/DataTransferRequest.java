package com.patientRecTransferApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class DataTransferRequest extends BaseEntity{

    private Long requestingFacility;
    private Long recipientFacility;
    private String status;
    private LocalDateTime requestTime;
    private LocalDateTime responseTime;
    private String encryptedFilePath;
    private String metadataFilePath;

}