package com.zazabeyligisf.zazacord.controller;

import com.zazabeyligisf.zazacord.model.Chatroom;
import com.zazabeyligisf.zazacord.model.Message;
import com.zazabeyligisf.zazacord.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    final ChatService chatService;
    final SimpMessagingTemplate simpMessagingTemplate;

    //TODO ADD CHATROOMS
    //TODO ADD USERS ONLINE
    //TODO ADD FRIENDS
    //TODO ADD ID FEATURE TO ALL TYPES AND ACCEPT JSON INSTEAD OF PAYLOADS
    @Autowired
    public ChatController(ChatService chatService, SimpMessagingTemplate simpMessagingTemplate) {
        this.chatService = chatService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public Chatroom createChatroom() {
        return chatService.createChatroom();
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receivePublicMessage(@Payload Message message) {
        return message;
    }

    @MessageMapping("/private-message")
    public Message receivePrivateMessage(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private",message);// /user/{userName}/private
        return message;
    }
}
