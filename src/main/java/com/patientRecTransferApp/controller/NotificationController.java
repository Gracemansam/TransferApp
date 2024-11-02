package com.patientRecTransferApp.controller;

import com.patientRecTransferApp.entity.Notification;
import com.patientRecTransferApp.serviceImpl.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/getUnreadNotifications")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        return ResponseEntity.ok(notificationService.getUnreadNotifications());
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadNotificationCount() {
        long count = notificationService.getUnreadNotificationCount();
        return ResponseEntity.ok(count);
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping("/all-notification")
    public ResponseEntity<List<Notification>> getAllNotificationsForUser() {
        List<Notification> notifications = notificationService.getAllNotificationsForUser();
        return ResponseEntity.ok(notifications);
    }
}