package org.oportuniza.oportunizabackend.chat.dtos;

import org.oportuniza.oportunizabackend.chat.models.ChatMessage;

import java.util.List;

public record MessagesDTO(List<ChatMessage> messages) {
}
