package com.zazabeyligisf.zazacord.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zazabeyligisf.zazacord.model.Chatroom;
import com.zazabeyligisf.zazacord.model.Message;
import com.zazabeyligisf.zazacord.model.User;
import com.zazabeyligisf.zazacord.repositories.ChatroomRepo;
import com.zazabeyligisf.zazacord.repositories.MessageRepo;
import com.zazabeyligisf.zazacord.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class ChatService {
    final Gson gson;
    final ChatroomRepo chatroomRepo;
    final MessageRepo messageRepo;
    final UserRepository userRepository;

    @Autowired
    public ChatService(Gson gson, ChatroomRepo chatroomRepo, MessageRepo messageRepo, UserRepository userRepository) {
        this.gson = gson;
        this.chatroomRepo = chatroomRepo;
        this.messageRepo = messageRepo;
        this.userRepository = userRepository;
    }

    public Message receivePrivateMessage(String message) {
        JsonObject payload = gson.fromJson(message, JsonObject.class);
        Message message1 = Message.builder()
                .chatroomID(UUID.fromString(payload.get("chatroomID").getAsString()))
                .messageID(UUID.randomUUID())
                .senderName(UUID.fromString(payload.get("senderID").getAsString()))
                .receiverName(UUID.fromString(payload.get("receiverID").getAsString()))
                .message(payload.get("content").getAsString())
                .dateTime(LocalDateTime.now())
                .build();

        messageRepo.save(message1);
        Chatroom foundChatRoom = chatroomRepo.findById(message1.getChatroomID()).orElseThrow();
        foundChatRoom.getMessageIDs().add(message1.getMessageID());
        return message1;
    }

    public void createChatroom(String payload) {
        JsonObject jsonObject = gson.fromJson(payload, JsonObject.class);
        List<UUID> friends = IntStream.range(0, jsonObject.get("userIDs").getAsJsonArray().size())
                .mapToObj(jsonObject.get("userIDs").getAsJsonArray()::get)
                .map(JsonElement::getAsString)
                .map(UUID::fromString)
                .toList();

        for (UUID friend : friends) {
            userRepository.findById(friend).orElseThrow();
        }

        System.out.println("friends = " + friends);

        chatroomRepo.save(Chatroom.builder()
                .messageIDs(new LinkedList<UUID>())
                .userIDs(friends)
                .build());
    }

    public void createUser(String user) {
        JsonObject payload = gson.fromJson(user, JsonObject.class);
        if (payload.has("id")) {
            userRepository.update(userRepository.findById(UUID.fromString(payload.get("id").getAsString())).orElseThrow(), gson.fromJson(user, JsonObject.class));

        } else {
            userRepository.save(User.builder()
                    .friends(new LinkedList<UUID>())
                    .id(UUID.randomUUID())
                    .password(payload.get("password").getAsString())
                    .username(payload.get("username").getAsString())
                    .build());
        }
    }
}
