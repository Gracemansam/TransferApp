package com.patientRecTransferApp.converter;

import com.patientRecTransferApp.dto.RegisterDto;
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


    public RegisterDto convertAppUserEntityToDTO(AppUser appUser){
        RegisterDto registerDto = new RegisterDto();
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
