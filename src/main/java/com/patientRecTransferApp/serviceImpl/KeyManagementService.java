package com.patientRecTransferApp.serviceImpl;

import org.springframework.stereotype.Service;

import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Service
public class KeyManagementService {

    private Map<String, KeyPair> facilityKeyPairs = new HashMap<>();

    public void generateKeyPairForFacility(String facilityId) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        facilityKeyPairs.put(facilityId, keyPair);
    }

    public String getEncodedPublicKey(Long facilityId) {
        PublicKey publicKey = getPublicKey(facilityId.toString());
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public void verifyPublicKey(Long facilityId, String encodedPublicKey) throws Exception {
        String storedPublicKey = getEncodedPublicKey(facilityId);
        if (!storedPublicKey.equals(encodedPublicKey)) {
            throw new SecurityException("Public key verification failed for facility: " + facilityId);
        }
    }

    public byte[] encryptKeyForRecipient(SecretKey fileKey, Long recipientFacilityId) throws Exception {
        PublicKey publicKey = getPublicKey(recipientFacilityId.toString());
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(fileKey.getEncoded());
    }

    public SecretKey decryptKeyForRecipient(byte[] encryptedKey, Long recipientFacilityId) throws Exception {
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