package org.oportuniza.oportunizabackend.chat.services;

import org.oportuniza.oportunizabackend.chat.dtos.MessageDTO;
import org.oportuniza.oportunizabackend.chat.models.ChatMessage;
import org.oportuniza.oportunizabackend.chat.models.ChatMessageRepository;
import org.oportuniza.oportunizabackend.chat.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {
    private final ChatMessageRepository messageRepository;

    public ChatMessageService(ChatMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(ChatMessage.MessageStatus.RECEIVED);
        messageRepository.save(chatMessage);
        return chatMessage;
    }

    /**
     * Gets new messages received from sender to receiver
     * @param senderId who sent the message
     * @param receiverId who is to receive
     * @return number of new messages
     */
    public long countNewMessages(Long senderId, Long receiverId) {
        return messageRepository.countBySenderAndReceiverAndStatus(
                senderId, receiverId, ChatMessage.MessageStatus.RECEIVED);
    }

    /**
     * Get messages from sender to receiver
     * @param sender who sent the message
     * @param receiver who received the message
     * @return the list of messages
     */
    public List<ChatMessage> findChatMessages(Long sender, Long receiver) {
        var messages = messageRepository.findBySenderAndReceiver(sender, receiver);

        if(!messages.isEmpty()) {
            updateStatuses(sender, receiver, ChatMessage.MessageStatus.DELIVERED);
        }

        return messages;
    }

    /**
     * Finds message given its id
     * @param id id of the message
     * @return the found message (or throw an error)
     */
    public MessageDTO findById(Long id) {
        Optional<ChatMessage> message = messageRepository.findById(id);
        if (message.isPresent()){
            ChatMessage msgObj = message.get();
            msgObj.setStatus(ChatMessage.MessageStatus.DELIVERED);
            messageRepository.save(msgObj);
            MessageDTO msg = new MessageDTO(
                    msgObj.getId(),
                    msgObj.getContent(),
                    msgObj.getSender(),
                    msgObj.getReceiver(),
                    msgObj.getTimestamp(),
                    msgObj.getStatus());
            return msg;

        } else {
            throw new ResourceNotFoundException("can't find message (" + id + ")");
        }
    }

    /**
     * Updates status of all messages from sender to receiver to a specified status
     * @param sender who sent the message
     * @param receiver who is to receive
     * @param status final state of all that message
     */
    public void updateStatuses(Long sender, Long receiver, ChatMessage.MessageStatus status) {
        List<ChatMessage> messages = findChatMessages(sender, receiver);
        for (ChatMessage message : messages) {
            message.setStatus(status);
            messageRepository.save(message);
        }
    }
}

