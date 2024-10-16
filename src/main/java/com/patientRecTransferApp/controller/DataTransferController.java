package com.patientRecTransferApp.controller;

import com.patientRecTransferApp.entity.DataTransferRequest;
import com.patientRecTransferApp.entity.DataTransferResponse;
import com.patientRecTransferApp.serviceImpl.DataTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/transfer")
public class DataTransferController {

    @Autowired
    private DataTransferService dataTransferService;
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @PostMapping("/request")
    public ResponseEntity<DataTransferResponse> requestData(@RequestBody DataTransferRequest request) {
        return ResponseEntity.ok(dataTransferService.requestTransfer(request));
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<DataTransferResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                           @RequestParam("requestId") Long requestId,
                                                           @RequestParam("senderFacilityId") Long senderFacilityId) throws Exception {
        DataTransferResponse response = dataTransferService.uploadData(file, requestId, senderFacilityId);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/download/{requestId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long requestId,
                                                 @RequestParam("recipientFacilityId") Long recipientFacilityId) throws Exception {
        Resource resource = dataTransferService.downloadData(requestId, recipientFacilityId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"decrypted_data.xlsx\"")
                .body(resource);
    }
}