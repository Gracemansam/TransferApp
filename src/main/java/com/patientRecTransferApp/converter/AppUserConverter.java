package com.patientRecTransferApp.converter;

import com.patientRecTransferApp.dto.RegisterDto;
import com.patientRecTransferApp.dto.response.AppUserDTO;
import com.patientRecTransferApp.entity.AppUser;
import com.patientRecTransferApp.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class AppUserConverter {

    public AppUser convertDTOtoAppUserEntity(RegisterDto registerDto){
        AppUser appUser = new AppUser();
        appUser.setFirstName(registerDto.getFirstName());
        appUser.setLastName(registerDto.getLastName());
        appUser.setPhoneNumber(registerDto.getPhoneNumber());
        appUser.setEmail(registerDto.getEmail());
        appUser.setGender(registerDto.getGender());
      //  appUser.setPassword(registerDto.getPassword());
        appUser.setUsername(registerDto.getUsername());

        return appUser;
    }

    public AppUser toEntity(AppUserDTO appUserDTO){
        AppUser appUser = new AppUser();
        appUser.setFirstName(appUserDTO.getFirstName());
        appUser.setLastName(appUserDTO.getLastName());
        appUser.setPhoneNumber(appUserDTO.getPhoneNumber());
        appUser.setEmail(appUserDTO.getEmail());
        appUser.setGender(appUserDTO.getGender());
        //  appUser.setPassword(registerDto.getPassword());
      //  appUser.setUsername(registerDto.getUsername());

        return appUser;
    }

    public AppUserDTO toDTO(AppUser appUser){
        AppUserDTO registerDto = new AppUserDTO();
       // registerDto.setId(appUser.getId());
        registerDto.setFirstName(appUser.getFirstName());
        registerDto.setLastName(appUser.getLastName());
        registerDto.setPhoneNumber(appUser.getPhoneNumber());
        registerDto.setEmail(appUser.getEmail());
        registerDto.setGender(appUser.getGender());
     //   registerDto.setPassword(appUser.getPassword());
      //  registerDto.setUsername(appUser.getUsername());

        return registerDto;
    }

    public RegisterDto convertAppUserEntityToDTO(AppUser appUser){
        RegisterDto registerDto = new RegisterDto();
        registerDto.setId(appUser.getId());
        registerDto.setFirstName(appUser.getFirstName());
        registerDto.setLastName(appUser.getLastName());
        registerDto.setPhoneNumber(appUser.getPhoneNumber());
        registerDto.setEmail(appUser.getEmail());
        registerDto.setGender(appUser.getGender());
        registerDto.setPassword(appUser.getPassword());
        registerDto.setUsername(appUser.getUsername());

        return registerDto;
    }
}
