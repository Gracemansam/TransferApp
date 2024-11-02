package com.patientRecTransferApp.controller;

import com.patientRecTransferApp.converter.AppUserConverter;
import com.patientRecTransferApp.dto.HospitalDto;
import com.patientRecTransferApp.dto.RegisterDto;
import com.patientRecTransferApp.dto.response.AppUserDTO;
import com.patientRecTransferApp.dto.response.AuthResponse;
import com.patientRecTransferApp.dto.response.HospitalCountResponse;
import com.patientRecTransferApp.entity.AppUser;
import com.patientRecTransferApp.entity.Hospital;
import com.patientRecTransferApp.entity.Patient;
import com.patientRecTransferApp.service.AppUserService;
import com.patientRecTransferApp.serviceImpl.AppUserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;
    private final AppUserConverter appUserConverter;



    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @PutMapping("/user/{id}")
    public ResponseEntity<AppUserDTO> updateUser(@PathVariable Long id, @RequestBody AppUserDTO appUserDTO) {
        AppUser updatedUser = appUserService.updateActor(id, appUserConverter.toEntity(appUserDTO));
        return ResponseEntity.ok(appUserConverter.toDTO(updatedUser));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        appUserService.deleteAppUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<AppUser> getUser(@PathVariable Long id) {
        AppUser user = appUserService.getAppUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/patient/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        Patient updatedPatient = appUserService.updatePatient(id, patient);
        return ResponseEntity.ok(updatedPatient);
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @DeleteMapping("/patient/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        appUserService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/patient/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        Patient patient = appUserService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = appUserService.getAllPatient();
        return ResponseEntity.ok(patients);
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @PutMapping("/hospital/{id}")
    public ResponseEntity<Hospital> updateHospital(@PathVariable Long id, @RequestBody Hospital hospital) {
        Hospital updatedHospital = appUserService.updateHospital(id, hospital);
        return ResponseEntity.ok(updatedHospital);
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @DeleteMapping("/hospital/{id}")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
        appUserService.deleteHospital(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/hospital/{id}")
    public ResponseEntity<Hospital> getHospital(@PathVariable Long id) {
        Hospital hospital = appUserService.getHospitalById(id);
        return ResponseEntity.ok(hospital);
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/hospitals")
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        List<Hospital> hospitals = appUserService.getAllHospital();
        return ResponseEntity.ok(hospitals);
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/principal")
    public ResponseEntity<AppUser> getPrincipalUserDetails(Principal principal) {
        String email = principal.getName();
        AppUser appUser = appUserService.findByEmail(email);
        return ResponseEntity.ok(appUser);
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/by-name")
    public ResponseEntity<Hospital> getHospitalByName(@RequestParam String name) {
        Hospital hospital = appUserService.getHospitalByName(name);
        return ResponseEntity.ok(hospital);
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/current-user-hospital-id")
    public ResponseEntity<HospitalCountResponse> getCurrentUserHospitalId() {
    return ResponseEntity.ok(appUserService.getHospitalId());
    }
}