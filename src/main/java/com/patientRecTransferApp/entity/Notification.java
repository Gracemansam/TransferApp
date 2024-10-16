package com.patientRecTransferApp.entity;

import com.patientRecTransferApp.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class Notification extends BaseEntity {

    private Long recipientFacilityId;
    private String message;
    private boolean read;
    private Long requestId;


}