package com.patientRecTransferApp.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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



}


//@Transactional
//public AppUser registerPatient(AppUser patient) {
//    patient.setUserType(UserType.PATIENT);
//    patient.setPassword(passwordEncoder.encode(patient.getPassword()));
//    patient.addPermission("ROLE_PATIENT");
//    return appUserRepository.save(patient);
//}
//
//@Transactional
//public AppUser registerHospitalAdmin(AppUser admin, Long hospitalId) {
//    Hospital hospital = hospitalRepository.findById(hospitalId)
//            .orElseThrow(() -> new RuntimeException("Hospital not found"));
//
//    admin.setUserType(UserType.HOSPITAL_ADMIN);
//    admin.setPassword(passwordEncoder.encode(admin.getPassword()));
//    admin.setHospital(hospital);
//    admin.addPermission("ROLE_HOSPITAL_ADMIN");
//    return appUserRepository.save(admin);
//}
//
//public AppUser findByUsername(String username) {
//    return appUserRepository.findByUsername(username)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//}



//@Autowired
//private ActorService actorService;
//
//// In your controller method
//public ResponseEntity<Actor> updateActor(@PathVariable Long id, @RequestBody Actor updatedActor) {
//    try {
//        Actor updated = actorService.updateActor(id, updatedActor);
//        return ResponseEntity.ok(updated);
//    } catch (ConcurrentModificationException e) {
//        // Log the error
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
//    } catch (RuntimeException e) {
//        // Log the error
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//    }
//}


//
//
//package com.example.patienttransfer.service;
//
//import com.example.patienttransfer.entity.Actor;
//import com.example.patienttransfer.repository.ActorRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import jakarta.persistence.OptimisticLockException;
//import org.springframework.dao.DataAccessException;
//
//@Service
//public class ActorService {
//
//    private final ActorRepository actorRepository;
//
//    @Autowired
//    public ActorService(ActorRepository actorRepository) {
//        this.actorRepository = actorRepository;
//    }
//

//
//    // Other methods...
//}
//
//// Custom exception for concurrent modification
//class ConcurrentModificationException extends RuntimeException {
//    public ConcurrentModificationException(String message, Throwable cause) {
//        super(message, cause);
//    }
//}






//package com.hospital.transfer.service;
//
//import com.hospital.transfer.entity;
//import com.hospital.transfer.repository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.crypto.Cipher;
//import javax.crypto.SecretKey;
//import javax.crypto.SecretKeyFactory;
//import javax.crypto.spec.PBEKeySpec;
//import javax.crypto.spec.SecretKeySpec;
//import java.security.SecureRandom;
//import java.util.Base64;
//import java.time.LocalDateTime;
//
//@Service
//public class ExtendedConsentTransferService {
//
//    @Autowired
//    private PatientRepository patientRepository;
//
//    @Autowired
//    private HospitalRepository hospitalRepository;
//
//    @Autowired
//    private ConsentRequestRepository consentRequestRepository;
//
//    @Autowired
//    private FileTransferRepository fileTransferRepository;
//
//    @Autowired
//    private NotificationService notificationService;
//
//    @Transactional
//    public void requestAndProcessPatientRecord(Long patientId, Long requestingHospitalId, Long holdingHospitalId) {
//        // Step 1: Create consent request
//        ConsentRequest consentRequest = createConsentRequest(patientId, requestingHospitalId, holdingHospitalId);
//        // Step 2: Notify patient
//        notifyPatient(consentRequest);
//        // Step 3: Wait for patient approval (this would typically be handled asynchronously)
//        // For demonstration, we'll assume the patient approves immediately
//        boolean patientApproved = true;
//        if (patientApproved) {
//            // Step 4: Process approval and initiate file transfer
//            FileTransfer fileTransfer = processApprovalAndInitiateTransfer(consentRequest);
//            // Step 5: Encrypt and transfer file
//            encryptAndTransferFile(fileTransfer);
//            // Step 6: Send decryption key to patient
//            sendDecryptionKeyToPatient(fileTransfer);
//        } else {
//            // Handle rejection
//            handleRejection(consentRequest);
//        }
//    }
//
//    private ConsentRequest createConsentRequest(Long patientId, Long requestingHospitalId, Long holdingHospitalId) {
//        Patient patient = patientRepository.findById(patientId)
//                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
//        Hospital requestingHospital = hospitalRepository.findById(requestingHospitalId)
//                .orElseThrow(() -> new EntityNotFoundException("Requesting hospital not found"));
//        Hospital holdingHospital = hospitalRepository.findById(holdingHospitalId)
//                .orElseThrow(() -> new EntityNotFoundException("Holding hospital not found"));
//        ConsentRequest consentRequest = new ConsentRequest();
//        consentRequest.setPatient(patient);
//        consentRequest.setRequestingHospital(requestingHospital);
//        consentRequest.setHoldingHospital(holdingHospital);
//        consentRequest.setRequestDate(LocalDateTime.now());
//        consentRequest.setStatus("PENDING");
//        return consentRequestRepository.save(consentRequest);
//    }
//
//    private void notifyPatient(ConsentRequest consentRequest) {
//        // In a real application, this would send an email, SMS, or push notification
//        notificationService.sendNotificationToPatient(consentRequest.getPatient(),
//                "Record transfer request from " + consentRequest.getRequestingHospital().getName());
//    }
//
//    private FileTransfer processApprovalAndInitiateTransfer(ConsentRequest consentRequest) {
//        consentRequest.setStatus("APPROVED");
//        consentRequest.setResponseDate(LocalDateTime.now());
//        consentRequestRepository.save(consentRequest);
//        FileTransfer fileTransfer = new FileTransfer();
//        fileTransfer.setPatient(consentRequest.getPatient());
//        fileTransfer.setSourceHospital(consentRequest.getHoldingHospital());
//        fileTransfer.setDestinationHospital(consentRequest.getRequestingHospital());
//        fileTransfer.setConsentRequest(consentRequest);
//        fileTransfer.setTransferDate(LocalDateTime.now());
//        fileTransfer.setTransferStatus("INITIATED");
//        fileTransfer.setEncryptionSalt(generateSalt());
//        return fileTransferRepository.save(fileTransfer);
//    }
//
//    private void encryptAndTransferFile(FileTransfer fileTransfer) {
//        // In a real application, this would retrieve the actual patient record
//        byte[] patientRecord = retrievePatientRecord(fileTransfer.getPatient(), fileTransfer.getSourceHospital());
//        try {
//            String encryptedContent = encryptFile(patientRecord, fileTransfer.getPatient().getEncryptedSecretAnswer(), fileTransfer.getEncryptionSalt());
//            fileTransfer.setEncryptedFile(encryptedContent.getBytes());
//            fileTransfer.setTransferStatus("COMPLETED");
//            fileTransferRepository.save(fileTransfer);
//            // Send encrypted file to destination hospital
//            sendEncryptedFile(fileTransfer);
//        } catch (Exception e) {
//            fileTransfer.setTransferStatus("FAILED");
//            fileTransferRepository.save(fileTransfer);
//            throw new RuntimeException("File encryption failed", e);
//        }
//    }
//
//    private void sendDecryptionKeyToPatient(FileTransfer fileTransfer) {
//        String decryptionKey = fileTransfer.getEncryptionSalt();
//        notificationService.sendDecryptionKeyToPatient(fileTransfer.getPatient(), decryptionKey);
//    }
//
//    private void handleRejection(ConsentRequest consentRequest) {
//        consentRequest.setStatus("REJECTED");
//        consentRequest.setResponseDate(LocalDateTime.now());
//        consentRequestRepository.save(consentRequest);
//        notificationService.notifyHospitalOfRejection(consentRequest.getRequestingHospital(), consentRequest.getPatient());
//    }
//
//    private byte[] retrievePatientRecord(Patient patient, Hospital hospital) {
//        // This method would interact with the hospital's system to retrieve the patient's record
//        // For demonstration, we're returning a dummy record
//        return "Patient record content".getBytes();
//    }
//
//    private void sendEncryptedFile(FileTransfer fileTransfer) {
//        // This method would handle the actual file transfer to the destination hospital
//        // For demonstration, we're just logging the action
//        System.out.println("Encrypted file sent to " + fileTransfer.getDestinationHospital().getName());
//    }
//
//    // Encryption methods (encryptFile, decryptFile, generateKey, generateSalt) remain the same as in the previous example
//
//    // ... (include the encryption methods here)
//}
///
//}