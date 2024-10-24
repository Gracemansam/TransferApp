package com.patientRecTransferApp.repository;

import com.patientRecTransferApp.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientFacilityIdAndReadFalse(Long recipientFacilityId);
}