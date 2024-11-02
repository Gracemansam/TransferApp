package com.patientRecTransferApp.controller;

import com.patientRecTransferApp.converter.AppUserConverter;
import com.patientRecTransferApp.dto.HospitalDto;
import com.patientRecTransferApp.dto.LoginDto;
import com.patientRecTransferApp.dto.RegisterDto;
import com.patientRecTransferApp.dto.response.AuthResponse;
import com.patientRecTransferApp.entity.AppUser;
import com.patientRecTransferApp.entity.Hospital;
import com.patientRecTransferApp.service.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AppUserService appUserService;
    private  final AppUserConverter appUserConverter;

    @PostMapping("/register/patient")
    public ResponseEntity<AuthResponse> registerPatient(@RequestBody RegisterDto registerDto) {
        return appUserService.registerPatient(registerDto);
    }

    @PostMapping("/register/hospital-admin/{hospitalId}")
    public ResponseEntity<AuthResponse> registerHospitalAdmin(@RequestBody RegisterDto registerDto, @PathVariable Long hospitalId) {
        return appUserService.registerHospitalAdmin(registerDto, hospitalId);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        return appUserService.login(loginDto.getEmail(), loginDto.getPassword(), request);
    }

    @PostMapping("/register/hospital")
    public ResponseEntity<Hospital> registerHospital(@RequestBody HospitalDto hospitalDto) {
        return appUserService.registerHospital(hospitalDto);
    }

    @GetMapping("/hospitals")
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        List<Hospital> hospitals = appUserService.getAllHospital();
        return ResponseEntity.ok(hospitals);
    }

    @GetMapping("/principal")
    public ResponseEntity<RegisterDto> getPrincipalUserDetails(Principal principal) {
        if (principal == null) {
            System.out.println("Principal is null");
            throw new NullPointerException("Principal is null");
        }
        String email = principal.getName();
        System.out.println("Principal email: " + email);
        AppUser appUser = appUserService.findByEmail(email);
       RegisterDto user= appUserConverter.convertAppUserEntityToDTO(appUser);

        return ResponseEntity.ok(user);
    }
}
