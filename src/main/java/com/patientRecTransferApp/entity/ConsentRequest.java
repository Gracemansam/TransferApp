package com.patientRecTransferApp.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consent_requests")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsentRequest extends BaseEntity {


    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "requesting_hospital_id", nullable = false)
    private Hospital requestingHospital;

    @ManyToOne
    @JoinColumn(name = "holding_hospital_id", nullable = false)
    private Hospital holdingHospital;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "status")
    private String status; // PENDING, APPROVED, DENIED

    @Column(name = "response_date")
    private LocalDateTime responseDate;

    // Getters and setters
}