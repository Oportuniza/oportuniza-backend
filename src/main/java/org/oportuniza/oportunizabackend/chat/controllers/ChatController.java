package org.oportuniza.oportunizabackend.chat.controllers;

import org.oportuniza.oportunizabackend.chat.dtos.MessageDTO;
import org.oportuniza.oportunizabackend.chat.models.ChatMessage;
import org.oportuniza.oportunizabackend.chat.services.ChatMessageService;
import org.oportuniza.oportunizabackend.notifications.services.NotificationService;
import org.oportuniza.oportunizabackend.users.exceptions.UserNotFoundException;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Controller class for handling chat-related functionality.
 */
@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final NotificationService notificationService;
    private final UserService userService;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService, NotificationService notificationService, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    /**
     * Receive a new chat message from the websocket
     * Saves the message and
     * Sends a notification to the receiver
     * @param chatMessage message received
     */
    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) throws UserNotFoundException {
        ChatMessage saved = chatMessageService.save(chatMessage);
        var sender = userService.getUserById(chatMessage.getSender());
        notificationService.sendNotification("Recebeu uma nova mensagem de " + sender.getName(), chatMessage.getReceiver());
        messagingTemplate.convertAndSendToUser(
                String.valueOf(chatMessage.getReceiver()),"/queue/messages",
                new MessageDTO(
                        saved.getId(),
                        saved.getContent(),
                        saved.getSender(),
                        saved.getReceiver(),
                        saved.getTimestamp(),
                        saved.getStatus()));
    }
}
