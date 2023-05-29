package com.zazabeyligisf.zazacord.services;

import com.github.javafaker.Faker;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
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

    public String createChatroom(String payload) throws MalformedURLException {
        JsonObject jsonObject = gson.fromJson(payload, JsonObject.class);
        String you = jsonObject.get("you").getAsString();
        UUID id = UUID.randomUUID();
        URL url = new URL("http://xahin.xyz/chatrooms/" + id.toString());
        User youUser = userRepository.findUserByUsername(you).orElseThrow();
        Chatroom chatroom =Chatroom.builder()
                .messageIDs(new LinkedList<UUID>())
                .userIDs(new LinkedList<String>())
                .createdBy(youUser.getId())
                .link(url)
                .build();
        chatroom.setId(id);
        chatroomRepo.save(chatroom);
        JsonObject response = new JsonObject();
        response.add("old_array", gson.toJsonTree(youUser.getChatrooms()));
        response.add("new", gson.toJsonTree(id.toString()));
        youUser.getChatrooms().add(id);
        userRepository.update(youUser, gson.fromJson(gson.toJson(youUser), JsonObject.class));
        return gson.toJson(response);
    }

    public String createUser(String user) {
        JsonObject payload = gson.fromJson(user, JsonObject.class);
        Optional<User> foundUser = userRepository.findUserByUsername(payload.get("username").getAsString());
        if (foundUser.isPresent()) {
            if (!foundUser.get().getPassword().equals(payload.get("password").getAsString())) {
                throw new RuntimeException("Wrong password");
            }
            return foundUser.get().getFakeUsername();
        } else {
            Faker fake = new Faker();
            UUID id = UUID.randomUUID();
            String fakeSupername = fake.superhero().name();
            userRepository.save(User.builder()
                    .friends(new LinkedList<UUID>())
                    .id(id)
                    .password(payload.get("password").getAsString())
                    .username(payload.get("username").getAsString())
                    .fakeUsername(fakeSupername)
                    .chatrooms(new LinkedList<UUID>())
                    .build());
            return fakeSupername;
        }
    }
}
