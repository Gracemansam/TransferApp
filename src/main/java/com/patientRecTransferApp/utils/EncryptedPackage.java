package com.patientRecTransferApp.utils;

public class EncryptedPackage {
    private final String encryptedData;
    private final String metadata;

    public EncryptedPackage(String encryptedData, String metadata) {
        this.encryptedData = encryptedData;
        this.metadata = metadata;
    }

    public String getEncryptedData() { return encryptedData; }
    public String getMetadata() { return metadata; }
}