package com.patientRecTransferApp.controller;

import com.patientRecTransferApp.dto.HospitalDto;
import com.patientRecTransferApp.dto.RegisterDto;
import com.patientRecTransferApp.dto.response.AuthResponse;
import com.patientRecTransferApp.entity.Hospital;
import com.patientRecTransferApp.service.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AppUserService appUserService;

    @PostMapping("/register/patient")
    public ResponseEntity<AuthResponse> registerPatient(@RequestBody RegisterDto registerDto) {
        return appUserService.registerPatient(registerDto);
    }

    @PostMapping("/register/hospital-admin/{hospitalId}")
    public ResponseEntity<AuthResponse> registerHospitalAdmin(@RequestBody RegisterDto registerDto, @PathVariable Long hospitalId) {
        return appUserService.registerHospitalAdmin(registerDto, hospitalId);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestParam String email, @RequestParam String password, HttpServletRequest request) {
        return appUserService.login(email, password, request);
    }

    @PostMapping("/register/hospital")
    public ResponseEntity<Hospital> registerHospital(@RequestBody HospitalDto hospitalDto) {
        return appUserService.registerHospital(hospitalDto);
    }
}
