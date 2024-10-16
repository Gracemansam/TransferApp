package com.patientRecTransferApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class AuditLog extends BaseEntity {


    private String operation;
    private Long recipientFacilityId;
    private Long senderFacilityId;
    private LocalDateTime timestamp;

}