package com.zazabeyligisf.zazacord.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zazabeyligisf.zazacord.model.Chatroom;

public interface ChatroomRepo extends JpaRepository<Chatroom, UUID> {

}
