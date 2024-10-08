package org.oportuniza.oportunizabackend.chat.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

/**
 * Represents a chat message in the chat application.
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String content;
    private Long sender;
    private Long receiver;
    private Date timestamp;
    //private MessageType type;
    private MessageStatus status;

    /**
     * Enum representing the type of the chat message.
     */
    public enum MessageType {
        CHAT, LEAVE, JOIN
    }

    /**
     * Enum representing the status of the chat message
     */
    public enum MessageStatus {
        RECEIVED, DELIVERED
    }

}
