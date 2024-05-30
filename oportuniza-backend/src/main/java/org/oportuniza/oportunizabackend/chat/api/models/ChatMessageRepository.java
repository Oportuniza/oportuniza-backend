package org.oportuniza.oportunizabackend.chat.api.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, String> {

    // To get messages number of messages not yet received, or delivered
    long countBySenderReceiverAndStatus(
            String senderId, String recipientId, ChatMessage.MessageStatus status);

    //List<ChatMessage> findByChatId(String chatId);
}
