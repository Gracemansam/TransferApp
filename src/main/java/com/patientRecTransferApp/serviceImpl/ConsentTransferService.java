package com.patientRecTransferApp.serviceImpl;

import com.patientRecTransferApp.entity.*;
import com.patientRecTransferApp.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.time.LocalDateTime;

@Service
public class ConsentTransferService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private ConsentRequestRepository consentRequestRepository;

    @Autowired
    private FileTransferRepository fileTransferRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public ConsentRequest requestPatientConsent(Long patientId, Long requestingHospitalId, Long holdingHospitalId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        Hospital requestingHospital = hospitalRepository.findById(requestingHospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Requesting hospital not found"));
        Hospital holdingHospital = hospitalRepository.findById(holdingHospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Holding hospital not found"));

        ConsentRequest consentRequest = new ConsentRequest();
        consentRequest.setPatient(patient);
        consentRequest.setRequestingHospital(requestingHospital);
        consentRequest.setHoldingHospital(holdingHospital);
        consentRequest.setRequestDate(LocalDateTime.now());
        consentRequest.setStatus("PENDING");

        ConsentRequest savedRequest = consentRequestRepository.save(consentRequest);

        // Send email notification to patient
        emailService.sendConsentRequestNotification(patient.getEmail(), savedRequest);

        return savedRequest;
    }

    @Transactional
    public void processPatientConsent(Long consentRequestId, boolean approved) {
        ConsentRequest consentRequest = consentRequestRepository.findById(consentRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Consent request not found"));
        consentRequest.setStatus(approved ? "APPROVED" : "DENIED");
        consentRequest.setResponseDate(LocalDateTime.now());
        consentRequestRepository.save(consentRequest);

        if (approved) {
            initiateFileTransfer(consentRequest);
        }

        // Notify hospitals about the decision
        emailService.sendConsentDecisionNotification(consentRequest.getRequestingHospital().getEmail(), consentRequest);
        emailService.sendConsentDecisionNotification(consentRequest.getHoldingHospital().getEmail(), consentRequest);
    }

    @Transactional
    public void uploadAndEncryptFile(Long fileTransferId, byte[] fileContent) throws Exception {
        FileTransfer fileTransfer = fileTransferRepository.findById(fileTransferId)
                .orElseThrow(() -> new EntityNotFoundException("File transfer not found"));

        Patient patient = fileTransfer.getPatient();
        String salt = generateSalt();
        String encryptedContent = encryptFile(fileContent, patient.getSecretAnswer(), salt);

        fileTransfer.setEncryptedContent(encryptedContent);
        fileTransfer.setEncryptionSalt(salt);
        fileTransfer.setTransferStatus("ENCRYPTED");
        fileTransferRepository.save(fileTransfer);

        // Generate and encrypt the decryption key
        String decryptionKey = generateDecryptionKey(patient.getSecretAnswer(), salt);
        String encryptedDecryptionKey = encryptDecryptionKey(decryptionKey, patient.getSecretAnswer());

        // Send encrypted decryption key to patient
        emailService.sendDecryptionKeyToPatient(patient.getEmail(), encryptedDecryptionKey);
    }

    public byte[] decryptFile(Long fileTransferId, String providedDecryptionKey) throws Exception {
        FileTransfer fileTransfer = fileTransferRepository.findById(fileTransferId)
                .orElseThrow(() -> new EntityNotFoundException("File transfer not found"));

        Patient patient = fileTransfer.getPatient();
        String decryptedKey = decryptDecryptionKey(providedDecryptionKey, patient.getSecretAnswer());

        return decryptFile(fileTransfer.getEncryptedContent(), decryptedKey, fileTransfer.getEncryptionSalt());
    }

    private void initiateFileTransfer(ConsentRequest consentRequest) {
        FileTransfer fileTransfer = new FileTransfer();
        fileTransfer.setPatient(consentRequest.getPatient());
        fileTransfer.setSourceHospital(consentRequest.getHoldingHospital());
        fileTransfer.setDestinationHospital(consentRequest.getRequestingHospital());
        fileTransfer.setConsentRequest(consentRequest);
        fileTransfer.setTransferDate(LocalDateTime.now());
        fileTransfer.setTransferStatus("INITIATED");
        fileTransferRepository.save(fileTransfer);

        // Notify holding hospital to upload the file
        emailService.sendFileUploadRequest(consentRequest.getHoldingHospital().getEmail(), fileTransfer);
    }

    private String encryptFile(byte[] fileContent, String secretAnswer, String salt) throws Exception {
        SecretKey key = generateKey(secretAnswer, salt);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(fileContent);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private byte[] decryptFile(String encryptedContent, String secretAnswer, String salt) throws Exception {
        SecretKey key = generateKey(secretAnswer, salt);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedContent);
        return cipher.doFinal(decodedBytes);
    }

    private SecretKey generateKey(String secretAnswer, String salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(secretAnswer.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String generateDecryptionKey(String secretAnswer, String salt) {
        // Combine secret answer and salt to create a unique decryption key
        return secretAnswer + ":" + salt;
    }

    private String encryptDecryptionKey(String decryptionKey, String secretAnswer) throws Exception {
        SecretKey key = generateKey(secretAnswer, generateSalt());
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(decryptionKey.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private String decryptDecryptionKey(String encryptedDecryptionKey, String secretAnswer) throws Exception {
        SecretKey key = generateKey(secretAnswer, generateSalt());
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedDecryptionKey);
        return new String(cipher.doFinal(decodedBytes));
    }
}