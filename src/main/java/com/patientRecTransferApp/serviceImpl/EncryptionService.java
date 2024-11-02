package com.patientRecTransferApp.serviceImpl;
import com.patientRecTransferApp.utils.EncryptedPackage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import org.json.JSONObject;

@Service
public class EncryptionService {
    private static final String AES_GCM_NOPADDING = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    @Autowired
    private KeyManagementService keyManagementService;

    @Autowired
    private AuditService auditService;

    public EncryptedPackage encryptData(String data, Long senderFacilityId, Long recipientFacilityId) throws Exception {
        // Generate a random AES key for file encryption
        SecretKey fileKey = generateAESKey();

        // Encrypt the actual data with AES key
        byte[] encryptedData = encryptWithAesGcm(data.getBytes(), fileKey);

        // Encrypt the AES key with recipient's public key so they can decrypt the file
        byte[] encryptedKey = keyManagementService.encryptKeyForRecipient(fileKey, recipientFacilityId);

        // Get sender's public key for verification
        String senderPublicKey = keyManagementService.getEncodedPublicKey(senderFacilityId);

        // Create digital signature of the encrypted data using sender's private key
        byte[] signature = keyManagementService.createDigitalSignature(encryptedData, senderFacilityId);

        // Create metadata including sender info, encrypted key, and signature
        String metadata = createMetadata(senderFacilityId, recipientFacilityId, encryptedKey,
                senderPublicKey, signature);

        auditService.logEncryption(senderFacilityId, recipientFacilityId);

        return new EncryptedPackage(Base64.getEncoder().encodeToString(encryptedData), metadata);
    }

    public String decryptData(EncryptedPackage encryptedPackage, Long recipientFacilityId) throws Exception {
        JSONObject jsonMetadata = new JSONObject(encryptedPackage.getMetadata());

        // Extract all necessary information from metadata
        Long senderFacilityId = jsonMetadata.getLong("senderFacilityId");
        byte[] encryptedKey = Base64.getDecoder().decode(jsonMetadata.getString("encryptedKey"));
        String receivedPublicKey = jsonMetadata.getString("senderPublicKey");
        byte[] signature = Base64.getDecoder().decode(jsonMetadata.getString("signature"));
        byte[] encryptedData = Base64.getDecoder().decode(encryptedPackage.getEncryptedData());

        // Verify sender's identity and public key
        keyManagementService.verifyPublicKey(senderFacilityId, receivedPublicKey);

        // Verify the digital signature
        boolean isValidSignature = keyManagementService.verifyDigitalSignature(
                encryptedData, signature, senderFacilityId);
        if (!isValidSignature) {
            throw new SecurityException("Invalid digital signature from sender facility: " + senderFacilityId);
        }

        // Decrypt the AES key using recipient's private key
        SecretKey fileKey = keyManagementService.decryptKeyForRecipient(encryptedKey, recipientFacilityId);

        // Decrypt the actual data using the AES key
        byte[] decryptedData = decryptWithAesGcm(encryptedData, fileKey);

        auditService.logDecryption(senderFacilityId, recipientFacilityId);

        return new String(decryptedData);
    }

    private String createMetadata(Long senderFacilityId, Long recipientFacilityId,
                                  byte[] encryptedKey, String senderPublicKey, byte[] signature) {
        JSONObject metadata = new JSONObject();
        metadata.put("senderFacilityId", senderFacilityId);
        metadata.put("recipientFacilityId", recipientFacilityId);
        metadata.put("encryptedKey", Base64.getEncoder().encodeToString(encryptedKey));
        metadata.put("senderPublicKey", senderPublicKey);
        metadata.put("signature", Base64.getEncoder().encodeToString(signature));
        metadata.put("timestamp", System.currentTimeMillis());
        return metadata.toString();
    }



    private SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    private byte[] encryptWithAesGcm(byte[] data, SecretKey key) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] cipherText = cipher.doFinal(data);
        byte[] encrypted = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encrypted, 0, iv.length);
        System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);

        return encrypted;
    }

    private byte[] decryptWithAesGcm(byte[] encryptedData, SecretKey key) throws Exception {
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, GCM_IV_LENGTH);
        byte[] cipherText = Arrays.copyOfRange(encryptedData, GCM_IV_LENGTH, encryptedData.length);

        Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        return cipher.doFinal(cipherText);
    }



    private byte[] extractKeyFromMetadata(String metadata) throws JSONException {
        JSONObject jsonMetadata = new JSONObject(metadata);
        String encodedKey = jsonMetadata.getString("encryptedKey");
        return Base64.getDecoder().decode(encodedKey);
    }
}

