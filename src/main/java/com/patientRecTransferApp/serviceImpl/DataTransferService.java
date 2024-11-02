package com.patientRecTransferApp.serviceImpl;

import com.patientRecTransferApp.dto.response.DataTransferResponses;
import com.patientRecTransferApp.dto.response.FileDownloadDTO;
import com.patientRecTransferApp.dto.response.HospitalCountResponse;
import com.patientRecTransferApp.entity.*;
import com.patientRecTransferApp.exception.ResourceNotFoundException;
import com.patientRecTransferApp.repository.AppUserRepository;
import com.patientRecTransferApp.repository.DataTransferRepository;

import com.patientRecTransferApp.repository.HospitalRepository;
import com.patientRecTransferApp.security.CustomUserDetailsService;
import com.patientRecTransferApp.serviceImpl.EncryptionService;
import com.patientRecTransferApp.utils.EncryptedPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DataTransferService {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private DataTransferRepository dataTransferRepository;

    @Autowired
    private AppUserRepository appUserRepository;




    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private HospitalRepository hospitalRepository;

    @Value("${file.upload.dir}")
    private String fileUploadDir;


    public DataTransferResponse requestTransfer(DataTransferRequest request) {
        Long userId = getCurrentUserId();
        Optional<AppUser> findUser = appUserRepository.findById(userId);
        if (!findUser.isPresent()) {
            throw new RuntimeException("No user found");

        }
        Hospital requestingHospital = findUser.get().getHospital();
        request.setRequestingFacility(requestingHospital.getId());
        if (request.getRequestingFacility() .equals(request.getRecipientFacility())) {
            throw new IllegalArgumentException("You cannot send a request to yourself.");
        }

        request.setAppUserId(userId);
        request.setStatus("PENDING");
        request.setRequestTime(LocalDateTime.now());


        DataTransferRequest savedRequest = dataTransferRepository.save(request);
        notificationService.sendNotification(request.getRecipientFacility(), " New data transfer request from " + requestingHospital.getName()
                + ".Message: " + request.getMessage(), request.getId());


        return new DataTransferResponse("Transfer request created successfully", "SUCCESS", savedRequest.getId());
    }

    /**
     * Upload data method - Called by the Recipient Facility (B) to upload data
     * for the Requesting Facility (A)
     */
    public DataTransferResponse uploadData(MultipartFile file, Long requestId, Long uploaderFacilityId)
            throws Exception {
        // Find the transfer request
        DataTransferRequest request = dataTransferRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Transfer request not found"));

        // Verify uploader is the recipient facility
        if (!request.getRecipientFacility().equals(uploaderFacilityId)) {
            throw new SecurityException("Only the recipient facility can upload data for this request");
        }

        // Read the Excel file
        String fileContent = excelService.readExcelFile(file.getInputStream());

        // Encrypt data for the requesting facility (they will decrypt it)
        // Note: requesting facility is the recipient of the encrypted data
        EncryptedPackage encryptedPackage = encryptionService.encryptData(
                fileContent,
                uploaderFacilityId,          // Recipient facility (B) is the sender of the data
                request.getRequestingFacility() // Requesting facility (A) will decrypt the data
        );

        // Generate unique filenames
        String encryptedFileName = UUID.randomUUID().toString() + "_encrypted.dat";
        String metadataFileName = UUID.randomUUID().toString() + "_metadata.json";

        // Save encrypted data and metadata
        Path encryptedFilePath = Paths.get(fileUploadDir, encryptedFileName);
        Path metadataFilePath = Paths.get(fileUploadDir, metadataFileName);

        if (Files.notExists(encryptedFilePath.getParent())) {
            Files.createDirectories(encryptedFilePath.getParent());
        }

        Files.write(encryptedFilePath, encryptedPackage.getEncryptedData().getBytes());
        Files.write(metadataFilePath, encryptedPackage.getMetadata().getBytes());

        // Update request status
        request.setStatus("COMPLETED");
        request.setResponseTime(LocalDateTime.now());
        request.setEncryptedFilePath(encryptedFilePath.toString());
        request.setMetadataFilePath(metadataFilePath.toString());
        dataTransferRepository.save(request);

        // Notify requesting facility that data is ready
        notificationService.sendNotification(request.getRequestingFacility(),
                "Data has been uploaded for your request " + requestId, request.getId());

        return new DataTransferResponse("Data uploaded and encrypted successfully", "SUCCESS", requestId);
    }

    /**
     * Download data method - Called by the Requesting Facility (A) to download
     * and decrypt their requested data
     */
    public FileDownloadDTO downloadData(Long requestId, Long downloaderFacilityId) throws Exception {
        // Input validation
        if (requestId == null || downloaderFacilityId == null) {
            throw new IllegalArgumentException("Request ID and downloader facility ID must not be null");
        }

        // Find and validate the transfer request
        DataTransferRequest request = dataTransferRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer request not found"));

        // Security checks
        validateRequest(request, downloaderFacilityId);

        // Validate file paths
        validateFilePaths(request);

        // Process the file
        byte[] fileContent = processFile(request, downloaderFacilityId);

        // Generate a meaningful filename
        String fileName = generateFileName(request);

        return new FileDownloadDTO(fileContent, fileName);
    }

    private void validateRequest(DataTransferRequest request, Long downloaderFacilityId) {
        if (!request.getRequestingFacility().equals(downloaderFacilityId)) {
            throw new SecurityException("Only the requesting facility can download this data");
        }

        if (!"COMPLETED".equals(request.getStatus())) {
            throw new IllegalStateException("Data has not yet been uploaded for this request");
        }
    }

    private byte[] processFile(DataTransferRequest request, Long downloaderFacilityId) throws Exception {
        // Read encrypted data and metadata
        String encryptedData = new String(Files.readAllBytes(Paths.get(request.getEncryptedFilePath())));
        String metadata = new String(Files.readAllBytes(Paths.get(request.getMetadataFilePath())));

        // Create encrypted package
        EncryptedPackage encryptedPackage = new EncryptedPackage(encryptedData, metadata);

        // Decrypt the data
        String decryptedData = encryptionService.decryptData(encryptedPackage, downloaderFacilityId);

        // Convert to Excel format
        return excelService.writeExcelFile(decryptedData);
    }

    private String generateFileName(DataTransferRequest request) {
        // Generate a meaningful filename using request details
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("data_transfer_%s_%s.xlsx", request.getId(), timestamp);
    }

    private void validateFilePaths(DataTransferRequest request) {
        if (request.getEncryptedFilePath() == null || request.getMetadataFilePath() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "File paths are missing in the request");
        }

        Path encryptedPath = Paths.get(request.getEncryptedFilePath());
        Path metadataPath = Paths.get(request.getMetadataFilePath());

        if (!Files.exists(encryptedPath) || !Files.isReadable(encryptedPath)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Encrypted file not found or not readable");
        }

        if (!Files.exists(metadataPath) || !Files.isReadable(metadataPath)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Metadata file not found or not readable");
        }
    }

public long getRequestsByStatus(String status, Long appUserId) {

        return dataTransferRepository.countByStatusAndAppUserId(status, appUserId);
    }

    public List<DataTransferResponses> getPendingRequestsByRecipientFacility() {
        HospitalCountResponse hospitalId = getHospitalId();
        List<DataTransferRequest> requests = dataTransferRepository.findPendingByRecipientFacilityId(hospitalId.getHosiptalId());

        List<DataTransferResponses> responses = new ArrayList<>();
        for (DataTransferRequest request : requests) {
            DataTransferResponses response = new DataTransferResponses();
            response.setRequestId(request.getId());
            response.setDateTime(request.getRequestTime());
            response.setRequestingFacility(request.getRequestingFacility());
            response.setRecipientFacility(request.getRecipientFacility());
            String facilityName = getFacilityNameById(response.getRequestingFacility());
            response.setDescription("New data request from " + facilityName);

            responses.add(response);
        }

        return responses;
    }

    public String getFacilityNameById(Long facilityId) {
        return hospitalRepository.findById(facilityId)
                .map(Hospital::getName)
                .orElse("Unknown Facility");
    }

    public List<DataTransferResponses> getCompletedRequestsByRecipientFacility() {
        HospitalCountResponse hospitalId = getHospitalId();
        List<DataTransferRequest> requests = dataTransferRepository.findCompletedByRequestingFacilityId(hospitalId.getHosiptalId());

        List<DataTransferResponses> responses = new ArrayList<>();
        for (DataTransferRequest request : requests) {
            DataTransferResponses response = new DataTransferResponses();
            response.setRequestId(request.getId());
            response.setDateTime(request.getResponseTime());
            response.setRequestingFacility(request.getRequestingFacility());
            response.setRecipientFacility(request.getRecipientFacility());
            String facilityName = getFacilityNameById(response.getRecipientFacility());
            response.setDescription("New data  from " + facilityName);

            responses.add(response);
        }

        return responses;
    }

    public DataTransferRequest findLatestPendingRequest(Long requestingFacility, Long recipientFacility) {
        return dataTransferRepository.findTopByRequestingFacilityAndRecipientFacilityAndStatusOrderByRequestTimeDesc(
                requestingFacility, recipientFacility, "PENDING");
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NullPointerException("User is not authenticated");
        }
        String email = authentication.getName();
        System.out.println("Principal email: " + email);
        Optional<AppUser> appUser = appUserRepository.findByEmail(email);
        if (appUser.isEmpty()) {
            throw new NullPointerException("AppUser not found");
        }
        return appUser.get().getId();
    }

    public HospitalCountResponse getHospitalId(){
        Long userId = getCurrentUserId();
        Optional<AppUser> findUser = appUserRepository.findById(userId);
        if (!findUser.isPresent()) {
            throw new RuntimeException("No user found");

        }
        HospitalCountResponse countResponse = new HospitalCountResponse();
        countResponse.setHosiptalId(findUser.get().getHospital().getId());
        return countResponse;
    }

    public List<DataTransferRequest> getAllRequestByRecipientId() {
        HospitalCountResponse hospitalId = getHospitalId();
        return dataTransferRepository.findByRecipientFacilityAndStatus(hospitalId.getHosiptalId(),"PENDING");
    }




}