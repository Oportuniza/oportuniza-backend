package org.oportuniza.oportunizabackend.chat.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Long> {

    // To get messages number of messages not yet received, or delivered
    long countBySenderAndReceiverAndStatus(
            Long senderId, Long recipientId, ChatMessage.MessageStatus status);

    // To get messages sent from sender to receiver
    List<ChatMessage> findBySenderAndReceiver(Long senderId, Long receiverId);
}
