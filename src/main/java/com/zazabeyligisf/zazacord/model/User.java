package com.zazabeyligisf.zazacord.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedList;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@Table(name = "user")
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String username;
    @ElementCollection
    private LinkedList<String> friends;
    private String password;

}
