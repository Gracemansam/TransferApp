package com.patientRecTransferApp.service;

import com.patientRecTransferApp.dto.HospitalDto;
import com.patientRecTransferApp.dto.RegisterDto;
import com.patientRecTransferApp.dto.response.AuthResponse;
import com.patientRecTransferApp.entity.AppUser;
import com.patientRecTransferApp.entity.Hospital;
import com.patientRecTransferApp.entity.Patient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AppUserService {
    ResponseEntity<AuthResponse> registerHospitalAdmin(RegisterDto registerDto, Long hospitalId);

    ResponseEntity<AuthResponse> registerPatient(RegisterDto registerDto);

    ResponseEntity<AuthResponse> login(String email, String password, HttpServletRequest request);

    ResponseEntity<Hospital>registerHospital(HospitalDto hospitalDto);

    @Transactional
    AppUser updateActor(Long id, AppUser appUser);

    void deleteAppUser(Long id);

    AppUser getAppUserById(Long id);

    Patient updatePatient(Long id, Patient patient);

    void  deletePatient(Long id);

    Patient getPatientById(Long id);

    Hospital getHospitalById(Long id);

    Hospital updateHospital(Long id, Hospital hospital);

    void deleteHospital(Long id);

    List<Patient> getAllPatient();

    List<Hospital> getAllHospital();
}
