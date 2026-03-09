package com.stellerainn.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Tag {
    private Long id;
    private Long userId;
    private String name;
    private String color;
    private LocalDateTime createdAt;
}
