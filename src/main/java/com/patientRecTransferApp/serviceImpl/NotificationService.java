package com.patientRecTransferApp.serviceImpl;

import com.patientRecTransferApp.dto.response.HospitalCountResponse;
import com.patientRecTransferApp.entity.AppUser;
import com.patientRecTransferApp.entity.Hospital;
import com.patientRecTransferApp.entity.Notification;
import com.patientRecTransferApp.repository.AppUserRepository;
import com.patientRecTransferApp.repository.HospitalRepository;
import com.patientRecTransferApp.repository.NotificationRepository;
import com.patientRecTransferApp.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public void sendNotification(Long recipientFacilityId, String message, Long requestId) {
        Optional<Hospital> checkHospital = hospitalRepository.findById(recipientFacilityId);
        if (checkHospital.isEmpty()){
            throw new RuntimeException(" Recipient facility does not exist");
        }
        Notification notification = new Notification();
        notification.setRecipientFacilityId(recipientFacilityId);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setRequestId(requestId);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications() {
        HospitalCountResponse hospitalId =getHospitalId();
        return notificationRepository.findByRecipientFacilityIdAndReadFalse(hospitalId.getHosiptalId());
    }

    public long getUnreadNotificationCount() {
        HospitalCountResponse hospitalId = getHospitalId();
        return notificationRepository.countByRecipientFacilityIdAndReadFalse(hospitalId.getHosiptalId());
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public HospitalCountResponse getHospitalId(){
        Long userId = getCurrentUserId();
        Optional<AppUser> findUser = appUserRepository.findById(userId);
        if (!findUser.isPresent()) {
            throw new RuntimeException("No user found");

        }
        HospitalCountResponse countResponse = new HospitalCountResponse();
        countResponse.setHosiptalId(findUser.get().getHospital().getId());
        return countResponse;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NullPointerException("User is not authenticated");
        }
        String email = authentication.getName();
        System.out.println("Principal email: " + email);
        Optional<AppUser> appUser = appUserRepository.findByEmail(email);
        if (appUser.isEmpty()) {
            throw new NullPointerException("AppUser not found");
        }
        return appUser.get().getId();
    }

    public List<Notification> getAllNotificationsForUser() {
        HospitalCountResponse hospitalId = getHospitalId();
        return notificationRepository.findByRecipientFacilityId(hospitalId.getHosiptalId());
    }
}