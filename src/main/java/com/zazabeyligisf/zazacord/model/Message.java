package com.zazabeyligisf.zazacord.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

enum Status {
    JOIN, MESSAGE, LEAVE
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private String senderName;
    private String receiverName;
    private String message;
}
