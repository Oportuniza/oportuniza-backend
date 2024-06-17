package org.oportuniza.oportunizabackend.chat.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.oportuniza.oportunizabackend.applications.dto.ApplicationDTO;
import org.oportuniza.oportunizabackend.chat.dtos.HistoryDTO;
import org.oportuniza.oportunizabackend.chat.dtos.MessageDTO;
import org.oportuniza.oportunizabackend.chat.dtos.MessagesDTO;
import org.oportuniza.oportunizabackend.chat.dtos.NewMessageCountDTO;
import org.oportuniza.oportunizabackend.chat.models.ChatMessage;
import org.oportuniza.oportunizabackend.chat.models.ChatNotification;
import org.oportuniza.oportunizabackend.chat.services.ChatMessageService;
import org.oportuniza.oportunizabackend.utils.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.Time;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for handling chat-related functionality.
 */
@Controller
public class ChatController {
    private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
    }

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
