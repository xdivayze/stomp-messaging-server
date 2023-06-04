package com.zazabeyligisf.zazacord.controller;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zazabeyligisf.zazacord.model.Chatroom;
import com.zazabeyligisf.zazacord.model.Message;
import com.zazabeyligisf.zazacord.model.User;
import com.zazabeyligisf.zazacord.repositories.ChatroomRepo;
import com.zazabeyligisf.zazacord.repositories.MessageRepo;
import com.zazabeyligisf.zazacord.repositories.UserRepository;
import com.zazabeyligisf.zazacord.services.ChatService;

@RestController
@RequestMapping("api")
public class ChatController {
    final ChatService chatService;
    final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRepository userRepository;
    private final Gson gson;
    private final MessageRepo messageRepo;
    private final ChatroomRepo chatroomRepo;

    //TODO ADD CHATROOMS
    //TODO ADD USERS ONLINE
    //TODO ADD FRIENDS
    //TODO ADD ID FEATURE TO ALL TYPES AND ACCEPT JSON INSTEAD OF PAYLOADS
    @Autowired
    public ChatController(ChatService chatService, SimpMessagingTemplate simpMessagingTemplate,
                          UserRepository userRepository, Gson gson, MessageRepo messageRepo, ChatroomRepo chatroomRepo) {
        this.chatService = chatService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
        this.gson = gson;
        this.messageRepo = messageRepo;
        this.chatroomRepo = chatroomRepo;
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

    @GetMapping("/chatroom")
    public Boolean getChatroomHtml(@Param("id") String id) {
        id = id.trim();
        return chatroomRepo.findById(UUID.fromString(id)).isPresent();
    }

    @GetMapping("/get-users")
    public String getUsers(@Param("username") String username) {
        String response = gson.toJson(userRepository.findByNameStartingWithIgnoreCase(username));
        System.out.printf("Response: %s%n", response);
        return response;
    }

    @GetMapping("/get-user-by-id")
    public String getUserById(@Param("id") String id) {
        return gson.toJson(userRepository.findById(UUID.fromString(id)).orElseThrow());
    }

    @PostMapping("/create-chatroom")
    public String createChatroom(@RequestBody String payload) throws MalformedURLException {
        return chatService.createChatroom(payload);
    }

    @PostMapping("new-user")
    public String newUser(@RequestBody String user) {
        return chatService.createUser(user);
    }

    @MessageMapping("/chat")
    public String receivePrivateMessage(@RequestBody String message) throws InterruptedException {
        JsonObject messageJson = gson.fromJson(message, JsonObject.class);
        UUID chatroomID = UUID.fromString(messageJson.get("chatroomID").getAsString());
        UUID messageID = UUID.randomUUID();

        Message message1 = Message.builder()
                .messageID(messageID)
                .message(messageJson.get("message").getAsString())
                .dateTime(LocalDateTime.now())
                .senderName(messageJson.get("senderName").getAsString())
                .chatroomID(chatroomID)
                .build();
        messageRepo.save(message1);
        Chatroom foundroom= chatroomRepo.findById(chatroomID).orElseThrow();
        foundroom.getMessageIDs().add(messageID);
        chatroomRepo.save(foundroom);

        System.out.printf("Message: %s%n", message);
        JsonObject response = new JsonObject();
        response.addProperty("senderName", message1.getSenderName());
        response.addProperty("message", message1.getMessage());
        simpMessagingTemplate.convertAndSend("/topic/chatroom/" + chatroomID, gson.toJson(response));
        return gson.toJson(response);
    }
}
