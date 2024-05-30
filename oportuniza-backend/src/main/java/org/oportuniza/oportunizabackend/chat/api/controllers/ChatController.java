package org.oportuniza.oportunizabackend.chat.api.controllers;

import org.oportuniza.oportunizabackend.chat.api.models.ChatMessage;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * Controller class for handling chat-related functionality.
 */
@Controller
public class ChatController {

    /**
     * Registers a user for chat.
     *
     * param chatMessage The chat message containing the sender's information.
     * param headerAccessor The SimpMessageHeaderAccessor object used to access session attributes.
     * return The registered chat message.
     */
    @MessageMapping("/chat.register") // Maps messages sent to /app/chat.register to this method.
    @SendTo("/topic/public")
    public ChatMessage register(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    /**
     * Sends a chat message to all connected users.
     *
     * param chatMessage The chat message to be sent.
     * return The sent chat message.
     */
    @MessageMapping("/chat.send") // Maps messages sent to /app/chat.send to this method.
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }
}
