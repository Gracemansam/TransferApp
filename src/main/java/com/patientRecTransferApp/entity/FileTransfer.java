package com.patientRecTransferApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_transfers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileTransfer  extends BaseEntity{


    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "source_hospital_id", nullable = false)
    private Hospital sourceHospital;

    @ManyToOne
    @JoinColumn(name = "destination_hospital_id", nullable = false)
    private Hospital destinationHospital;

    @OneToOne
    @JoinColumn(name = "consent_request_id", nullable = false)
    private ConsentRequest consentRequest;

    @Column(name = "transfer_date")
    private LocalDateTime transferDate;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "encryption_salt")
    private String encryptionSalt;

    @Column(name = "transfer_status")
    private String transferStatus;

    @Lob
    @Column(name = "encrypted_file")
    private byte[] encryptedFile;


    // Getters and setters
}
