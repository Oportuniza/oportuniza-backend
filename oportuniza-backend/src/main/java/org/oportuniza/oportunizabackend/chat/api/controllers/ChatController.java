package org.oportuniza.oportunizabackend.chat.api.controllers;

import org.oportuniza.oportunizabackend.chat.api.models.ChatMessage;
import org.oportuniza.oportunizabackend.chat.api.models.ChatNotification;
import org.oportuniza.oportunizabackend.chat.services.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller class for handling chat-related functionality.
 */
@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatMessageService chatMessageService;

    /**
     * Receive a new chat message from the websocket
     * Saves the message and
     * Sends a notification to the receiver
     * @param chatMessage message received
     */
    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        ChatMessage saved = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiver(),"/queue/messages",
                new ChatNotification(
                        saved.getId(),
                        saved.getSender()));
    }

    /**
     * Get number of new messages from sender to receiver
     * @param sender who sent the message
     * @param receiver who is to receive the message
     * @return ResponseEntity represents the whole HTTP response: status code, headers, and body
     */
    @GetMapping("/messages/{sender}/{receiver}/count")
    public ResponseEntity<Long> countNewMessages(
            @PathVariable String sender,
            @PathVariable String receiver) {

        return ResponseEntity
                .ok(chatMessageService.countNewMessages(sender, receiver));
    }

    /**
     * Get chat messages
     * @param sender who sent the message
     * @param receiver who is to receive the message
     * @return ResponseEntity represents the whole HTTP response: status code, headers, and body
     */
    @GetMapping("/messages/{sender}/{receiver}")
    public ResponseEntity<?> findChatMessages (@PathVariable String sender,
                                               @PathVariable String receiver) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(sender, receiver));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage (@PathVariable String id) {
        return ResponseEntity
                .ok(chatMessageService.findById(id));
    }
}
