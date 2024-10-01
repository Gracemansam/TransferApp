package com.patientRecTransferApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patients")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patient extends AppUser {

    @Column(name = "medical_record_number", unique = true)
    private String medicalRecordNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "secret_question")
    private String secretQuestion;

    @Column(name = "encrypted_secret_answer")
    private String secretAnswer;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FileTransfer> fileTransfers;

    @PostPersist
    public void addPatientRole() {
        this.addRole("ROLE_PATIENT");
    }
}