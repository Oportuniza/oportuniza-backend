package org.oportuniza.oportunizabackend.notifications.services;

import org.oportuniza.oportunizabackend.notifications.dtos.NotificationDTO;
import org.oportuniza.oportunizabackend.notifications.model.GeneralNotification;
import org.oportuniza.oportunizabackend.notifications.model.GeneralNotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final GeneralNotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(GeneralNotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(String message, String targetUser) {
        GeneralNotification notification = new GeneralNotification(message, targetUser);
        messagingTemplate.convertAndSendToUser(
                targetUser,"/notifications",
                notification);
        notificationRepository.save(notification);
    }

    public List<GeneralNotification> getNotifications(String targetUser) {
        return this.notificationRepository.findByTargetUser(targetUser);
    }

    public GeneralNotification createNotification(NotificationDTO notification) {
        GeneralNotification notificationEntity = new GeneralNotification();
        notificationEntity.setMessage(notification.msg());
        notificationEntity.setTargetUser(notification.targetUser());
        return notificationRepository.save(notificationEntity);
    }
}
