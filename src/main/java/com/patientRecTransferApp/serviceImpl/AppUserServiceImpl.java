package com.patientRecTransferApp.serviceImpl;

import com.patientRecTransferApp.common_constant.CommonConstant;
import com.patientRecTransferApp.converter.AppUserConverter;
import com.patientRecTransferApp.converter.PatientConverter;
import com.patientRecTransferApp.dto.HospitalDto;
import com.patientRecTransferApp.dto.RegisterDto;
import com.patientRecTransferApp.dto.response.AuthResponse;
import com.patientRecTransferApp.entity.AppUser;
import com.patientRecTransferApp.entity.Hospital;
import com.patientRecTransferApp.entity.Patient;
import com.patientRecTransferApp.entity.UserType;
import com.patientRecTransferApp.exception.BusinessException;
import com.patientRecTransferApp.exception.ErrorModel;
import com.patientRecTransferApp.repository.AppUserRepository;
import com.patientRecTransferApp.repository.HospitalRepository;
import com.patientRecTransferApp.repository.PatientRepository;
import com.patientRecTransferApp.security.JwtTokenProvider;
import com.patientRecTransferApp.service.AppUserService;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

import javax.crypto.Cipher;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final AppUserConverter appUserConverter;
    private final PasswordEncoder passwordEncoder;
    private final HospitalRepository hospitalRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PatientConverter patientConverter;
    private final PatientRepository patientRepository;

    private static final String AES_KEY = generateAESKey(256);


    public static String generateAESKey(int keySize)  {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(keySize);
            SecretKey secretKey = keyGen.generateKey();
            byte[] encoded = secretKey.getEncoded();
            return Base64.getEncoder().encodeToString(encoded);

        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException("AES key generation failed", e);
        }

    }

@Override
    public ResponseEntity<AuthResponse> registerHospitalAdmin(RegisterDto registerDto, Long hospitalId) {

        Optional<AppUser> isEmailExist = appUserRepository.findByEmail(registerDto.getEmail());

        if (isEmailExist.isPresent()) {
            ErrorModel errorModel = ErrorModel.builder()
                    .code(CommonConstant.USER_ALREADY_EXIST_CODE)
                    .message(CommonConstant.USER_ALREADY_EXIST)
//					.timestamp(LocalDateTime.now())
                    .build();
            throw new BusinessException(errorModel);


        }

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        // Create new user
        AppUser newUser = appUserConverter.convertDTOtoAppUserEntity(registerDto);
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        newUser.addPermission("ROLE_HOSPITAL_ADMIN");
        newUser.setUserType(UserType.HOSPITAL_ADMIN);
        newUser.setHospital(hospital);
        appUserRepository.save(newUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(newUser.getEmail(), newUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var jwtToken = jwtTokenProvider.generateToken(authentication);
        var authResponse = AuthResponse.builder()
                .jwt(jwtToken)
                .status(true)
                .build();
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);


    }






@Override
        public ResponseEntity<AuthResponse> registerPatient(RegisterDto registerDto) {
            Optional<AppUser> isEmailExist = appUserRepository.findByEmail(registerDto.getEmail());

            if (isEmailExist.isPresent()) {
                ErrorModel errorModel = ErrorModel.builder()
                        .code(CommonConstant.USER_ALREADY_EXIST_CODE)
                        .message(CommonConstant.USER_ALREADY_EXIST)
                        .build();
                throw new BusinessException(errorModel);
            }

            Patient newUser = patientConverter.convertDTOtoPatientEntity(registerDto);
            newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            newUser.setSecretQuestion(registerDto.getSecretQuestion());

            String encryptedSecretAnswer = encryptSecretAnswer(registerDto.getSecretAnswer());
            newUser.setSecretAnswer(encryptedSecretAnswer);

            newUser.addPermission("ROLE_PATIENT");
            newUser.setUserType(UserType.PATIENT);
            patientRepository.save(newUser);

            Authentication authentication = new UsernamePasswordAuthenticationToken(newUser.getEmail(), newUser.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            var jwtToken = jwtTokenProvider.generateToken(authentication);
            var authResponse = AuthResponse.builder()
                    .jwt(jwtToken)
                    .status(true)
                    .build();
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
        }

        private String encryptSecretAnswer(String secretAnswer) {
            try {
                SecretKeySpec secretKey = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] encryptedBytes = cipher.doFinal(secretAnswer.getBytes(StandardCharsets.UTF_8));
                return Base64.getEncoder().encodeToString(encryptedBytes);
            } catch (Exception e) {
                throw new RuntimeException("Error encrypting secret answer", e);
            }
        }

        public String decryptSecretAnswer(String encryptedSecretAnswer) {
            try {
                SecretKeySpec secretKey = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedSecretAnswer));
                return new String(decryptedBytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("Error decrypting secret answer", e);
            }
        }


@Override
    public ResponseEntity<AuthResponse> login(String email, String password, HttpServletRequest request) {
        Optional<AppUser> user = appUserRepository.findByEmail(email);
        if (user.isPresent()) {
            AppUser appUser = user.get();
            if (passwordEncoder.matches(password, appUser.getPassword())) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(appUser.getEmail(), appUser.getPassword());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                var jwtToken = jwtTokenProvider.generateToken(authentication);
                var authResponse = AuthResponse.builder()
                        .jwt(jwtToken)
                        .status(true)
                        .build();
                return new ResponseEntity<>(authResponse, HttpStatus.OK);
            } else {
                ErrorModel errorModel = ErrorModel.builder()
                        .code(CommonConstant.INVALID_CREDENTIALS_CODE)
                        .message(CommonConstant.INVALID_CREDENTIALS)
//					.timestamp(LocalDateTime.now())
                        .build();
                throw new BusinessException(errorModel);
            }
        } else {
            ErrorModel errorModel = ErrorModel.builder()
                    .code(CommonConstant.USER_NOT_FOUND_CODE)
                    .message(CommonConstant.USER_NOT_FOUND)
//				.timestamp(LocalDateTime.now())
                    .build();
            throw new BusinessException(errorModel);
        }

    }
@Override
    public ResponseEntity<Hospital>registerHospital(HospitalDto hospitalDto) {
        Hospital hospital = new Hospital();
        hospital.setName(hospitalDto.getHospitalName());
        hospital.setAddress(hospitalDto.getHospitalAddress());
        hospital.setContactNumber(hospitalDto.getHospitalContactNumber());
        hospital.setEmail(hospitalDto.getHospitalEmail());
        hospitalRepository.save(hospital);
        return new ResponseEntity<>(hospital, HttpStatus.CREATED);
    }

    @Transactional
    @Override
    public AppUser updateActor(Long id, AppUser appUser) {
        int maxRetries = 3;
        int attempts = 0;

        while (attempts < maxRetries) {
            try {
                AppUser existingAppUser = appUserRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("AppUser not found"));

                // Update the fields
                existingAppUser.setFirstName(appUser.getFirstName());
                existingAppUser.setLastName(appUser.getLastName());
                existingAppUser.setEmail(appUser.getEmail());

                // other fields.

                return appUserRepository.save(existingAppUser);
            } catch (OptimisticLockException | DataAccessException e) {
                attempts++;
                if (attempts >= maxRetries) {
                    throw new ConcurrentModificationException("Unable to update app user after " + maxRetries + " attempts due to concurrent modifications", e);
                }

                try {
                    Thread.sleep(100 * attempts);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted while waiting to retry", ie);
                }
            }
        }
        throw new RuntimeException("Unexpected error occurred while updating actor");
    }
@Override
    public void deleteAppUser(Long id) {
        appUserRepository.deleteById(id);
    }
@Override
    public AppUser getAppUserById(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AppUser not found"));
    }
    @Override

    public Patient updatePatient(Long id, Patient patient) {
        Optional<Patient> existingPatient = patientRepository.findById(id);
        if (existingPatient.isPresent()) {
            Patient updatedPatient = existingPatient.get();
            updatedPatient.setFirstName(patient.getFirstName());
            updatedPatient.setLastName(patient.getLastName());
            updatedPatient.setEmail(patient.getEmail());
            updatedPatient.setSecretQuestion(patient.getSecretQuestion());
            updatedPatient.setSecretAnswer(patient.getSecretAnswer());
            return patientRepository.save(updatedPatient);
        } else {
            throw new RuntimeException("Patient not found");
        }

    }
@Override
    public  void  deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
    @Override
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }
    @Override
    public Hospital getHospitalById(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
    }
@Override
    public Hospital updateHospital(Long id, Hospital hospital) {
        Optional<Hospital> existingHospital = hospitalRepository.findById(id);
        if (existingHospital.isPresent()) {
            Hospital updatedHospital = existingHospital.get();
            updatedHospital.setName(hospital.getName());
            updatedHospital.setAddress(hospital.getAddress());
            updatedHospital.setContactNumber(hospital.getContactNumber());
            updatedHospital.setEmail(hospital.getEmail());
            return hospitalRepository.save(updatedHospital);
        } else {
            throw new RuntimeException("Hospital not found");
        }
    }
@Override
    public void deleteHospital(Long id) {
        hospitalRepository.deleteById(id);
    }
@Override
    public List<Patient> getAllPatient() {
        return patientRepository.findAll();
    }
    @Override
    public List<Hospital> getAllHospital() {
        return hospitalRepository.findAll();
    }

}
