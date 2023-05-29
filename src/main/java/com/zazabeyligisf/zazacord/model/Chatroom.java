package com.zazabeyligisf.zazacord.model;

import com.google.gson.Gson;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "chatroom")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chatroom {
    public Chatroom(LinkedList<UUID> messages) {
        this.messageIDs = messages;
    }
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;
    @ElementCollection
    private List<String> userIDs;

    @Column(name = "created_by")
    private UUID createdBy;
    @Column(name = "link")
    private URL link;

    @ElementCollection
    private List<UUID> messageIDs;
}