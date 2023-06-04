package com.zazabeyligisf.zazacord.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zazabeyligisf.zazacord.model.Message;


public interface MessageRepo extends JpaRepository<Message, UUID> {
}
