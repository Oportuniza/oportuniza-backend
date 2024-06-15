package org.oportuniza.oportunizabackend.chat.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.oportuniza.oportunizabackend.applications.dto.ApplicationDTO;
import org.oportuniza.oportunizabackend.chat.dtos.MessageDTO;
import org.oportuniza.oportunizabackend.chat.dtos.MessagesDTO;
import org.oportuniza.oportunizabackend.chat.dtos.NewMessageCountDTO;
import org.oportuniza.oportunizabackend.chat.models.ChatMessage;
import org.oportuniza.oportunizabackend.chat.models.ChatNotification;
import org.oportuniza.oportunizabackend.chat.services.ChatMessageService;
import org.oportuniza.oportunizabackend.utils.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Controller class for handling chat-related functionality.
 */
@Controller
public class ChatController {

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

    /**
     * Get number of new messages from sender to receiver
     * @param sender who sent the message
     * @param receiver who is to receive the message
     * @return ResponseEntity represents the whole HTTP response: status code, headers, and body
     */
    @GetMapping("/messages/{sender}/{receiver}/count")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get number of new messages from sender to receiver")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Number of new messages from sender to receiver", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = NewMessageCountDTO.class))
            })
    })
    public NewMessageCountDTO countNewMessages(
            @Parameter(description = "The ID of the user who send the message") @PathVariable Long sender,
            @Parameter(description = "The ID of the user who received the message") @PathVariable Long receiver) {

        long newMessages = chatMessageService.countNewMessages(sender, receiver);
        return new NewMessageCountDTO(newMessages);
    }

    /**
     * Get chat messages
     * @param sender who sent the message
     * @param receiver who is to receive the message
     * @return ResponseEntity represents the whole HTTP response: status code, headers, and body
     */
    @GetMapping("/messages/{sender}/{receiver}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get messages from sender to receiver")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages from sender to receiver", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = MessagesDTO.class))
            })
    })
    public MessagesDTO findChatMessages (
            @Parameter(description = "The ID of the user who send the message") @PathVariable Long sender,
            @Parameter(description = "The ID of the user who received the message") @PathVariable Long receiver) {
        List<ChatMessage> messages = chatMessageService.findChatMessages(sender, receiver);
        return new MessagesDTO(messages);
    }

    @GetMapping("/messages/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get message given its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message requested", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = MessageDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Message not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public MessageDTO findMessage (
            @Parameter(description = "The ID of the the message") @PathVariable Long id) {
        return chatMessageService.findById(id);
    }
}
