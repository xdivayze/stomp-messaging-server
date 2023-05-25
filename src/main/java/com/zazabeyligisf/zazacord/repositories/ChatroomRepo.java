package com.zazabeyligisf.zazacord.repositories;

import com.zazabeyligisf.zazacord.model.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatroomRepo extends JpaRepository<Chatroom, UUID> {

}
