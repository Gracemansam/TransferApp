package com.patientRecTransferApp.controller;

import com.patientRecTransferApp.entity.Notification;
import com.patientRecTransferApp.serviceImpl.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{facilityId}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long facilityId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(facilityId));
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}