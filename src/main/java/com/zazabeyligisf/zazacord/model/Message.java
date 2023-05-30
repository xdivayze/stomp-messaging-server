package com.zazabeyligisf.zazacord.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "message")
public class Message {
    @Id
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(org.hibernate.type.SqlTypes.UUID)
    private UUID messageID;
    private String senderName;
    private String message;
    private LocalDateTime dateTime;
    private UUID chatroomID;
}
