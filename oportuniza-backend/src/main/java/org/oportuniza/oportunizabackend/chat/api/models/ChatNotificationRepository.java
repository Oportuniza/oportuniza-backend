package org.oportuniza.oportunizabackend.chat.api.models;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatNotificationRepository extends JpaRepository<ChatNotification, String> {

}
