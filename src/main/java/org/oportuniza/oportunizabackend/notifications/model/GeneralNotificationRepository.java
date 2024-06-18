package org.oportuniza.oportunizabackend.notifications.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneralNotificationRepository extends JpaRepository<GeneralNotification, Long>  {
    List<GeneralNotification> findByTargetUser(Long targetUser);
}
