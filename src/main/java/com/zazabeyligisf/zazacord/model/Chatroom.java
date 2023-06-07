package com.zazabeyligisf.zazacord.model;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;
    @ElementCollection
    private List<String> userIDs;

    @Column(name = "created_by")
    private UUID createdBy;
    @Column(name = "link")
    private URL link;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<UUID> messageIDs;
    
}