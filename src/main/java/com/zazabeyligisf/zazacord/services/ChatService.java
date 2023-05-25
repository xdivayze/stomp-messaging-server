package com.zazabeyligisf.zazacord.services;

import com.google.gson.Gson;
import com.zazabeyligisf.zazacord.model.Chatroom;
import com.zazabeyligisf.zazacord.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    final Gson gson;

    @Autowired
    public ChatService(Gson gson) {
        this.gson = gson;
    }

    public Message receivePrivateMessage(Message message) {

        return message;
    }

    public Chatroom createChatroom() {

    }
}
