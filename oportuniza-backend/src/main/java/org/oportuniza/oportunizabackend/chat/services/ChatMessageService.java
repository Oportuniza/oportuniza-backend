package org.oportuniza.oportunizabackend.chat.services;

import org.oportuniza.oportunizabackend.authentication.api.models.MyUser;
import org.oportuniza.oportunizabackend.chat.api.models.ChatMessage;
import org.oportuniza.oportunizabackend.chat.api.models.ChatMessageRepository;
import org.oportuniza.oportunizabackend.chat.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {
    @Autowired private ChatMessageRepository messageRepository;
    //@Autowired private ChatRoomService chatRoomService;
    // @Autowired private MongoOperations mongoOperations;

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
        return messageRepository.countBySenderReceiverAndStatus(
                sender, receiver, ChatMessage.MessageStatus.RECEIVED);
    }

    // todo: necessary chat room??
    /**
     * Get messages from sender to receiver
     * @param sender who sent the message
     * @param receiver who received the message
     * @return the list of messages
     */
    public List<ChatMessage> findChatMessages(String sender, String receiver) {
        /*var chatId = chatRoomService.getChatId(senderId, recipientId, false);

        var messages =
                chatId.map(cId -> messageRepository.findByChatId(cId)).orElse(new ArrayList<>());

        if(messages.size() > 0) {
            updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }

        return messages;*/
        return new ArrayList<>();
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
    /*
    public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
        Query query = new Query(
                Criteria
                        .where("senderId").is(senderId)
                        .and("recipientId").is(recipientId));
        Update update = Update.update("status", status);
        mongoOperations.updateMulti(query, update, ChatMessage.class);
    }*/
}

