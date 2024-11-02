package com.patientRecTransferApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDTO {
    private String firstName;
    private String lastName;
    private String email;

    private String phoneNumber;

    private String gender;
    // Other fields

    // Getters and setters
}
