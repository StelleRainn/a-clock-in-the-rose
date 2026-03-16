package com.stellerainn.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private Long id;
    private Long sessionId;
    private String role; // "user" or "model"
    private String content;
    private LocalDateTime createdAt;
}
