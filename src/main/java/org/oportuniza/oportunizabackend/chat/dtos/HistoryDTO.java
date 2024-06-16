package org.oportuniza.oportunizabackend.chat.dtos;

import org.oportuniza.oportunizabackend.chat.models.ChatMessage;

import java.util.List;
import java.util.Map;

public record HistoryDTO (Map<Long, List<MessageDTO>> history){
}
