package com.zazabeyligisf.zazacord.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@Setter
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
    private List<UUID> friends;
    private String password;

}
