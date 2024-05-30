package org.oportuniza.oportunizabackend.chat.api.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, String> {

    // To get messages number of messages not yet received, or delivered
    long countBySenderAndReceiverAndStatus(
            String senderId, String recipientId, ChatMessage.MessageStatus status);

    // To get messages sent from sender to receiver
    List<ChatMessage> findBySenderAndReceiver(String sender, String receiver);
}
