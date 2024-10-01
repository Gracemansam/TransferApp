package com.patientRecTransferApp.controller;

import com.patientRecTransferApp.dto.HospitalDto;
import com.patientRecTransferApp.dto.RegisterDto;
import com.patientRecTransferApp.dto.response.AuthResponse;
import com.patientRecTransferApp.entity.AppUser;
import com.patientRecTransferApp.entity.Hospital;
import com.patientRecTransferApp.entity.Patient;
import com.patientRecTransferApp.service.AppUserService;
import com.patientRecTransferApp.serviceImpl.AppUserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;




    @PutMapping("/user/{id}")
    public ResponseEntity<AppUser> updateUser(@PathVariable Long id, @RequestBody AppUser appUser) {
        AppUser updatedUser = appUserService.updateActor(id, appUser);
        return ResponseEntity.ok(updatedUser);
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

    @DeleteMapping("/patient/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        appUserService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        Patient patient = appUserService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = appUserService.getAllPatient();
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/hospital/{id}")
    public ResponseEntity<Hospital> updateHospital(@PathVariable Long id, @RequestBody Hospital hospital) {
        Hospital updatedHospital = appUserService.updateHospital(id, hospital);
        return ResponseEntity.ok(updatedHospital);
    }

    @DeleteMapping("/hospital/{id}")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
        appUserService.deleteHospital(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hospital/{id}")
    public ResponseEntity<Hospital> getHospital(@PathVariable Long id) {
        Hospital hospital = appUserService.getHospitalById(id);
        return ResponseEntity.ok(hospital);
    }

    @GetMapping("/hospitals")
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        List<Hospital> hospitals = appUserService.getAllHospital();
        return ResponseEntity.ok(hospitals);
    }
}