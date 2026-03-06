package com.stellerainn.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    // New Profile Fields
    private String nickname;
    private String avatarUrl;
    private String bio;
    private String gender; // MALE, FEMALE, OTHER
    private String website;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
