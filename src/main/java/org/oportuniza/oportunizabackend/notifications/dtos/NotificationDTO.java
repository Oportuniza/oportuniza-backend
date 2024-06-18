package org.oportuniza.oportunizabackend.notifications.dtos;

public record NotificationDTO(
    String msg,
    Long targetUser
) {
}
