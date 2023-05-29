package com.zazabeyligisf.zazacord.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zazabeyligisf.zazacord.model.Message;
import com.zazabeyligisf.zazacord.model.User;
import com.zazabeyligisf.zazacord.repositories.UserRepository;
import com.zazabeyligisf.zazacord.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.MalformedURLException;
import java.util.UUID;

@Controller
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

    @GetMapping("/chatroom")
    public ModelAndView getChatroomHtml(@Param("id") String id) {
        ModelAndView modelAndView = new ModelAndView("chatroom");
        modelAndView.addObject("id", id);
        return modelAndView;
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
