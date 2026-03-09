package com.stellerainn.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Subtask {
    private Long id;
    private Long taskId;
    private String title;
    private Boolean completed;
    private LocalDateTime createdAt;
}
