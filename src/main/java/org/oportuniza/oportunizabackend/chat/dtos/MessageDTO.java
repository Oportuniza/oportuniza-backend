package org.oportuniza.oportunizabackend.chat.dtos;

import org.oportuniza.oportunizabackend.chat.models.ChatMessage;

import java.util.Date;

public record MessageDTO(
        Long id,
        String content,
        Long sender,
        Long receiver,
        Date timestamp,
        ChatMessage.MessageStatus status
) {
}