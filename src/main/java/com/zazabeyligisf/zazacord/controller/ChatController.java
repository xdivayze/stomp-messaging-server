package com.zazabeyligisf.zazacord.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zazabeyligisf.zazacord.model.Message;
import com.zazabeyligisf.zazacord.model.User;
import com.zazabeyligisf.zazacord.repositories.UserRepository;
import com.zazabeyligisf.zazacord.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api")
public class ChatController {
    final ChatService chatService;
    final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRepository userRepository;
    private final Gson gson;

    //TODO ADD CHATROOMS
    //TODO ADD USERS ONLINE
    //TODO ADD FRIENDS
    //TODO ADD ID FEATURE TO ALL TYPES AND ACCEPT JSON INSTEAD OF PAYLOADS
    @Autowired
    public ChatController(ChatService chatService, SimpMessagingTemplate simpMessagingTemplate,
                          UserRepository userRepository, Gson gson) {
        this.chatService = chatService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
        this.gson = gson;
    }

    @PostMapping("/get-user")
    public String getUser(@RequestBody String payload) {
        JsonObject jsonObject = gson.fromJson(payload, JsonObject.class);
        //if sent password is equal to the password in the database return uuid
        User user = userRepository.findUserByUsername(jsonObject.get("username").getAsString()).or(() -> {
            newUser(payload);
            return userRepository.findUserByUsername(jsonObject.get("username").getAsString());
        }).orElseThrow();
        if (user.getPassword().equals(jsonObject.get("password").getAsString())) {
            return user.getId().toString();
        }
        return "Passwords don't match";
    }

    @PostMapping("/create-chatroom")
    public void createChatroom(@RequestBody String payload) {
        chatService.createChatroom(payload);
    }

    @PostMapping("new-user")
    public void newUser(@RequestBody String user) {
        chatService.createUser(user);
    }

    @PostMapping("/add-friend")
    public void addFriend(@RequestBody String id, @Payload UUID friendID) {
        User user = userRepository.findById(UUID.fromString(id)).orElseThrow();
        //check if uuid is existing in friends
        user.getFriends().stream().map(UUID::toString).filter(friendID.toString()::equals).findAny().or(() -> {
            user.getFriends().add(friendID);
            return java.util.Optional.empty();
        });
        userRepository.update(user, gson.fromJson(gson.toJson(user), JsonObject.class));
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receivePublicMessage(@Payload Message message) {
        return message;
    }

    @MessageMapping("/private-message")
    public Message receivePrivateMessage(@RequestBody String message) {

        Message message1 = chatService.receivePrivateMessage(message);
        simpMessagingTemplate.convertAndSendToUser(message1.getChatroomID().toString(), "/private", message1.toString());// /user/{userName}/private
        return message1;
    }
}
