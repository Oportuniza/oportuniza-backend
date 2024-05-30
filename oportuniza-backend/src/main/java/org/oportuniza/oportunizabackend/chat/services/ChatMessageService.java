package org.oportuniza.oportunizabackend.chat.services;

import org.oportuniza.oportunizabackend.chat.api.models.ChatMessage;
import org.oportuniza.oportunizabackend.chat.api.models.ChatMessageRepository;
import org.oportuniza.oportunizabackend.chat.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {
    @Autowired private ChatMessageRepository messageRepository;

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(ChatMessage.MessageStatus.RECEIVED);
        messageRepository.save(chatMessage);
        return chatMessage;
    }

    /**
     * Gets new messages received from sender to receiver
     * @param sender who sent the message
     * @param receiver who is to receive
     * @return number of new messages
     */
    public long countNewMessages(String sender, String receiver) {
        return messageRepository.countBySenderAndReceiverAndStatus(
                sender, receiver, ChatMessage.MessageStatus.RECEIVED);
    }

    /**
     * Get messages from sender to receiver
     * @param sender who sent the message
     * @param receiver who received the message
     * @return the list of messages
     */
    public List<ChatMessage> findChatMessages(String sender, String receiver) {
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
    public ChatMessage findById(String id) {
        Optional<ChatMessage> message = messageRepository.findById(id);
        if (message.isPresent()){
            var msgObj = message.get();
            msgObj.setStatus(ChatMessage.MessageStatus.DELIVERED);
            return messageRepository.save(msgObj);

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
    public void updateStatuses(String sender, String receiver, ChatMessage.MessageStatus status) {
        List<ChatMessage> messages = findChatMessages(sender, receiver);
        for (ChatMessage message : messages) {
            message.setStatus(status);
            messageRepository.save(message);
        }
    }
}

