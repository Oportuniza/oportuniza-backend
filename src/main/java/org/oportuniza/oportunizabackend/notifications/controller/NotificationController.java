package org.oportuniza.oportunizabackend.notifications.controller;

import org.oportuniza.oportunizabackend.notifications.dtos.NotificationDTO;
import org.oportuniza.oportunizabackend.notifications.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationsService) {
        this.notificationService = notificationsService;
    }

    @GetMapping("/notifications/{targetUser}")
    public ResponseEntity<?> findNotifications (@PathVariable Long targetUser) {
        return ResponseEntity
                .ok(notificationService.getNotifications(targetUser));
    }

    @PostMapping("/notifications/add")
    public ResponseEntity<?> addNotification (@RequestBody NotificationDTO notification) {
        return ResponseEntity.ok(notificationService.createNotification(notification));
    }

    @GetMapping("/notifications/trigger/{userId}/{message}")
    public ResponseEntity<?> triggerNotification (@PathVariable Long userId, @PathVariable String message) {
        notificationService.sendNotification(message, userId);
        return ResponseEntity.ok(0);
    }
}
