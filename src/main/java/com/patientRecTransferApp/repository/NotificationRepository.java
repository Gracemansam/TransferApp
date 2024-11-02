package com.patientRecTransferApp.repository;

import com.patientRecTransferApp.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientFacilityIdAndReadFalse(Long recipientFacilityId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipientFacilityId = :recipientFacilityId AND n.read = false")
    long countByRecipientFacilityIdAndReadFalse(@Param("recipientFacilityId") Long recipientFacilityId);

    List<Notification> findByRecipientFacilityId(Long userId);
}