package com.stellerainn.backend.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserStats {
    private Long userId;
    private Integer level;
    private Integer currentXp;
    private Integer nextLevelXp;
    private Integer totalFocusMinutes;
    private Integer streakDays;
    private LocalDate lastFocusDate;
    private LocalDateTime updatedAt;
}
