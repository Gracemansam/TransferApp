package com.patientRecTransferApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsentRequestDto {
    private Long patientId;
    private Long requestingHospitalId;
    private Long holdingHospitalId;
}
