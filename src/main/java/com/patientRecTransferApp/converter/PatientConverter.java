package com.patientRecTransferApp.converter;

import com.patientRecTransferApp.dto.RegisterDto;
import com.patientRecTransferApp.entity.Patient;
import org.springframework.stereotype.Component;


@Component
public class PatientConverter {


    public Patient convertDTOtoPatientEntity(RegisterDto registerDto){
        Patient patientEntity = new Patient();
        patientEntity.setFirstName(registerDto.getFirstName());
        patientEntity.setLastName(registerDto.getLastName());
        patientEntity.setPhoneNumber(registerDto.getPhoneNumber());
        patientEntity.setEmail(registerDto.getEmail());
        patientEntity.setDateOfBirth(registerDto.getDateOfBirth());
        patientEntity.setGender(registerDto.getGender());
       // patientEntity.setPassword(registerDto.getPassword());
        patientEntity.setUsername(registerDto.getUsername());


        return patientEntity;
    }



    public RegisterDto convertPatientEntityToDTO(Patient patientEntity){
        RegisterDto registerDto = new RegisterDto();
        registerDto.setFirstName(patientEntity.getFirstName());
        registerDto.setLastName(patientEntity.getLastName());
        registerDto.setPhoneNumber(patientEntity.getPhoneNumber());
        registerDto.setEmail(patientEntity.getEmail());
        registerDto.setDateOfBirth(patientEntity.getDateOfBirth());
        registerDto.setGender(patientEntity.getGender());
        registerDto.setPassword(patientEntity.getPassword());
        return registerDto;
    }

}