package com.patientRecTransferApp.serviceImpl;

import com.patientRecTransferApp.entity.DataTransferRequest;
import com.patientRecTransferApp.entity.DataTransferResponse;
import com.patientRecTransferApp.entity.Notification;
import com.patientRecTransferApp.repository.DataTransferRepository;

import com.patientRecTransferApp.serviceImpl.EncryptionService;
import com.patientRecTransferApp.utils.EncryptedPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DataTransferService {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private DataTransferRepository dataTransferRepository;

    @Autowired
    private  NotificationService notificationService;
    @Autowired
    private ExcelService excelService;

    @Value("${file.upload.dir}")
    private String fileUploadDir;

    public DataTransferResponse requestTransfer(DataTransferRequest request) {
        request.setStatus("PENDING");
        request.setRequestTime(LocalDateTime.now());

        DataTransferRequest savedRequest = dataTransferRepository.save(request);
        notificationService.sendNotification(request.getRecipientFacility()," New data transfer request from "+ request.getRequestingFacility(),request.getId());


        return new DataTransferResponse("Transfer request created successfully", "SUCCESS", savedRequest.getId());
    }

    public DataTransferResponse uploadData(MultipartFile file, Long requestId, Long senderFacilityId) throws Exception {
        DataTransferRequest request = dataTransferRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Transfer request not found"));

        String fileContent = excelService.readExcelFile(file.getInputStream());

        EncryptedPackage encryptedPackage = encryptionService.encryptData(fileContent, senderFacilityId, request.getRecipientFacility());

        String encryptedFileName = UUID.randomUUID().toString() + "_encrypted.dat";
        String metadataFileName = UUID.randomUUID().toString() + "_metadata.json";

        Path encryptedFilePath = Paths.get(fileUploadDir, encryptedFileName);
        Files.write(encryptedFilePath, encryptedPackage.getEncryptedData().getBytes());

        Path metadataFilePath = Paths.get(fileUploadDir, metadataFileName);
        Files.write(metadataFilePath, encryptedPackage.getMetadata().getBytes());

        request.setStatus("COMPLETED");
        request.setResponseTime(LocalDateTime.now());
        request.setEncryptedFilePath(encryptedFilePath.toString());
        request.setMetadataFilePath(metadataFilePath.toString());
        dataTransferRepository.save(request);

        notificationService.sendNotification(request.getRecipientFacility()," Data transfer completed for "+ requestId,request.getId());


        return new DataTransferResponse("Data uploaded and encrypted successfully", "SUCCESS", requestId);
    }

    public Resource downloadData(Long requestId, Long recipientFacilityId) throws Exception {
        DataTransferRequest request = dataTransferRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Transfer request not found"));

        String encryptedData = new String(Files.readAllBytes(Paths.get(request.getEncryptedFilePath())));
        String metadata = new String(Files.readAllBytes(Paths.get(request.getMetadataFilePath())));

        EncryptedPackage encryptedPackage = new EncryptedPackage(encryptedData, metadata);

        String decryptedData = encryptionService.decryptData(encryptedPackage, recipientFacilityId);

        byte[] excelBytes = excelService.writeExcelFile(decryptedData);

        ByteArrayResource resource = new ByteArrayResource(excelBytes);

        return resource;
    }

    public List<DataTransferRequest> getRequestsByStatus(String status) {
        return dataTransferRepository.findByStatus(status);
    }

    public DataTransferRequest findLatestPendingRequest(Long requestingFacility, Long recipientFacility) {
        return dataTransferRepository.findTopByRequestingFacilityAndRecipientFacilityAndStatusOrderByRequestTimeDesc(
                requestingFacility, recipientFacility, "PENDING");
    }


}