package com.patientRecTransferApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadDTO {
    private byte[] fileContent;
    private String fileName;
}