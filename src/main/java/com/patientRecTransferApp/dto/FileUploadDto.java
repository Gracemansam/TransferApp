package com.patientRecTransferApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class FileUploadDto {
    private Long fileTransferId;
    private MultipartFile fileContent;
}
