package com.patientRecTransferApp.controller;

import com.patientRecTransferApp.dto.response.DataTransferResponses;
import com.patientRecTransferApp.dto.response.FileDownloadDTO;
import com.patientRecTransferApp.entity.DataTransferRequest;
import com.patientRecTransferApp.entity.DataTransferResponse;
import com.patientRecTransferApp.entity.Notification;
import com.patientRecTransferApp.serviceImpl.DataTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable Long requestId,
            @RequestParam("recipientFacilityId") Long recipientFacilityId) throws Exception {

        FileDownloadDTO fileData = dataTransferService.downloadData(requestId, recipientFacilityId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileData.getFileName() + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(fileData.getFileContent());
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/pending")
    public long getAllPendingRequests() {
        Long appUserId = dataTransferService.getCurrentUserId();
        return dataTransferService.getRequestsByStatus("PENDING", appUserId);
    }

    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/upload/pending")
    public List<DataTransferResponses> getAllPendingRequestsForUpload() {
        return dataTransferService.getPendingRequestsByRecipientFacility();
    }

    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/download/completed")
    public ResponseEntity<List<DataTransferResponses>> getAllCompletedRequestsForDownload() {
        return ResponseEntity.ok(dataTransferService.getCompletedRequestsByRecipientFacility()) ;
    }


    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/completed")
    public long getAllCompletedRequests() {
        Long appUserId = dataTransferService.getCurrentUserId();
        return dataTransferService.getRequestsByStatus("COMPLETED", appUserId);
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/latest-pending-request")
    public ResponseEntity<DataTransferRequest> getLatestPendingRequest(
            @RequestParam Long requestingFacility,
            @RequestParam Long recipientFacility) {
        DataTransferRequest request = dataTransferService.findLatestPendingRequest(requestingFacility, recipientFacility);
        return ResponseEntity.ok(request);
    }

    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/all-request")
    public ResponseEntity<List<DataTransferRequest>> getAllRequestByRecipientId() {
        List<DataTransferRequest> request = dataTransferService.getAllRequestByRecipientId();
        return ResponseEntity.ok(request);
    }



}