package com.patientRecTransferApp.serviceImpl;

import com.patientRecTransferApp.entity.Hospital;
import com.patientRecTransferApp.repository.HospitalRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Service
public class KeyManagementService {
    private Map<String, KeyPair> facilityKeyPairs = new HashMap<>();

    @Autowired
    private HospitalRepository hospitalRepository;

    @PostConstruct
    public void initializeKeyPairs() {
        List<Hospital> hospitals = hospitalRepository.findAll();
        for (Hospital hospital : hospitals) {
            try {
                if (!facilityKeyPairs.containsKey(hospital.getId().toString())) {
                    generateKeyPairForFacility(hospital.getId().toString());
                }
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Failed to generate key pair for facility: " + hospital.getId(), e);
            }
        }
    }

    public byte[] createDigitalSignature(byte[] data, Long senderFacilityId) throws Exception {
        PrivateKey privateKey = getPrivateKey(senderFacilityId.toString());
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public boolean verifyDigitalSignature(byte[] data, byte[] signatureBytes, Long senderFacilityId)
            throws Exception {
        PublicKey publicKey = getPublicKey(senderFacilityId.toString());
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }

    public void generateKeyPairForFacility(String facilityId) throws NoSuchAlgorithmException {
        if (!facilityKeyPairs.containsKey(facilityId)) {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            facilityKeyPairs.put(facilityId, keyPair);
        }
    }

    public void ensureKeyPairExists(Long facilityId) throws NoSuchAlgorithmException {
        if (!facilityKeyPairs.containsKey(facilityId.toString())) {
            generateKeyPairForFacility(facilityId.toString());
        }
    }

    public String getEncodedPublicKey(Long facilityId) throws NoSuchAlgorithmException {
        ensureKeyPairExists(facilityId);
        PublicKey publicKey = getPublicKey(facilityId.toString());
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public void verifyPublicKey(Long facilityId, String encodedPublicKey) throws Exception {
        ensureKeyPairExists(facilityId);
        String storedPublicKey = getEncodedPublicKey(facilityId);

        if (encodedPublicKey == null || encodedPublicKey.trim().isEmpty()) {
            throw new SecurityException("Received public key is null or empty for facility: " + facilityId);
        }

        if (!storedPublicKey.equals(encodedPublicKey)) {
            throw new SecurityException("Public key verification failed for facility: " + facilityId +
                    ". The provided key does not match the stored key.");
        }
    }

    public byte[] encryptKeyForRecipient(SecretKey fileKey, Long recipientFacilityId) throws Exception {
        ensureKeyPairExists(recipientFacilityId);
        PublicKey publicKey = getPublicKey(recipientFacilityId.toString());
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(fileKey.getEncoded());
    }

    public SecretKey decryptKeyForRecipient(byte[] encryptedKey, Long recipientFacilityId) throws Exception {
        ensureKeyPairExists(recipientFacilityId);
        PrivateKey privateKey = getPrivateKey(recipientFacilityId.toString());
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedKey = cipher.doFinal(encryptedKey);
        return new SecretKeySpec(decryptedKey, "AES");
    }

    private PublicKey getPublicKey(String facilityId) {
        KeyPair keyPair = facilityKeyPairs.get(facilityId);
        if (keyPair == null) {
            throw new IllegalStateException("No key pair found for facility: " + facilityId);
        }
        return keyPair.getPublic();
    }

    private PrivateKey getPrivateKey(String facilityId) {
        KeyPair keyPair = facilityKeyPairs.get(facilityId);
        if (keyPair == null) {
            throw new IllegalStateException("No key pair found for facility: " + facilityId);
        }
        return keyPair.getPrivate();
    }
}