package com.patientRecTransferApp.controller;


import com.patientRecTransferApp.dto.ConsentRequestDto;
import com.patientRecTransferApp.dto.FileUploadDto;
import com.patientRecTransferApp.entity.ConsentRequest;
import com.patientRecTransferApp.entity.FileTransfer;
import com.patientRecTransferApp.serviceImpl.ConsentTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/consent-transfer")
public class ConsentTransferController {

    @Autowired
    private ConsentTransferService consentTransferService;

    @PostMapping("/request-consent")
    public ResponseEntity<ConsentRequest> requestConsent(@RequestBody ConsentRequestDto requestDto) {
        ConsentRequest consentRequest = consentTransferService.requestPatientConsent(
                requestDto.getPatientId(),
                requestDto.getRequestingHospitalId(),
                requestDto.getHoldingHospitalId()
        );
        return ResponseEntity.ok(consentRequest);
    }

    @PostMapping("/process-consent/{consentRequestId}")
    public ResponseEntity<Void> processConsent(@PathVariable Long consentRequestId, @RequestParam boolean approved) {
        consentTransferService.processPatientConsent(consentRequestId, approved);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload-file")
    public ResponseEntity<Void> uploadFile(@RequestBody FileUploadDto uploadDto) {
        try {
            consentTransferService.uploadAndEncryptFile(uploadDto.getFileTransferId(), uploadDto.getFileContent().getBytes());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/decrypt-file/{fileTransferId}")
    public ResponseEntity<byte[]> decryptFile(@PathVariable Long fileTransferId, @RequestParam String decryptionKey) {
        try {
            byte[] decryptedContent = consentTransferService.decryptFile(fileTransferId, decryptionKey);
            return ResponseEntity.ok(decryptedContent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}