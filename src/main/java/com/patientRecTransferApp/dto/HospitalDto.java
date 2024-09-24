package com.patientRecTransferApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalDto {

    private String hospitalName;
    private String hospitalAddress;
    private String hospitalContactNumber;
    private String hospitalEmail;


}
