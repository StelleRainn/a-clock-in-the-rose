package com.stellerainn.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Achievement {
    private Long id;
    private String name;
    private String description;
    private String criteriaType;
    private Integer criteriaValue;
    private String iconName;
    private LocalDateTime createdAt;
    
    // For UserAchievement view
    private Boolean unlocked;
    private LocalDateTime unlockedAt;
}
