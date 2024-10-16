package com.patientRecTransferApp.controller;


import com.patientRecTransferApp.serviceImpl.KeyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/keys")
public class KeyManagementController {

    @Autowired
    private KeyManagementService keyManagementService;

    @PostMapping("/generate/{facilityId}")
    public ResponseEntity<String> generateKeyPair(@PathVariable String facilityId) {
        try {
            keyManagementService.generateKeyPairForFacility(facilityId);
            return ResponseEntity.ok("Key pair generated successfully for facility: " + facilityId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error generating key pair: " + e.getMessage());
        }
    }

    @GetMapping("/public/{facilityId}")
    public ResponseEntity<String> getPublicKey(@PathVariable Long facilityId) {
        try {
            String publicKey = keyManagementService.getEncodedPublicKey(facilityId);
            return ResponseEntity.ok(publicKey);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving public key: " + e.getMessage());
        }
    }

//    @PostMapping("/public/{facilityId}")
//    public ResponseEntity<String> addPublicKey(@PathVariable String facilityId, @RequestBody String publicKey) {
//        try {
//            keyManagementService.addPublicKeyForFacility(facilityId, publicKey);
//            return ResponseEntity.ok("Public key added successfully for facility: " + facilityId);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Error adding public key: " + e.getMessage());
//        }
//    }
}