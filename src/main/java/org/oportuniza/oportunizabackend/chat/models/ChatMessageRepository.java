package org.oportuniza.oportunizabackend.chat.models;

import org.oportuniza.oportunizabackend.chat.dtos.MessageDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Long> {

    // To get messages number of messages not yet received, or delivered
    long countBySenderAndReceiverAndStatus(
            Long senderId, Long recipientId, ChatMessage.MessageStatus status);

    // To get messages sent from sender to receiver
    List<ChatMessage> findBySenderAndReceiver(Long senderId, Long receiverId);

    //@Query("SELECT new org.oportuniza.oportunizabackend.chat.dtos.MessageDTO(c.id, c.content, c.sender, c.receiver, c.timestamp, c.status) FROM chat_message c WHERE c.sender = :userId or c.receiver = :userId ORDER BY c.timestamp")
    List<MessageDTO> findChatMessageByReceiverOrSenderOrderByTimestamp(Long sender, Long receiver);
}
