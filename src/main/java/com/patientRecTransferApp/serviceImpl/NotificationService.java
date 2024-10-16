package com.patientRecTransferApp.serviceImpl;

import com.patientRecTransferApp.entity.Hospital;
import com.patientRecTransferApp.entity.Notification;
import com.patientRecTransferApp.repository.HospitalRepository;
import com.patientRecTransferApp.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Notification> getUnreadNotifications(Long recipientFacility) {
        return notificationRepository.findByRecipientFacilityIdAndReadFalse(recipientFacility);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}