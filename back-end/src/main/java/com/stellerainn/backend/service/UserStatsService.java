package com.stellerainn.backend.service;

import com.stellerainn.backend.entity.UserStats;
import com.stellerainn.backend.mapper.UserStatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserStatsService {

    @Autowired
    private UserStatsMapper userStatsMapper;

    public UserStats getUserStats(Long userId) {
        UserStats stats = userStatsMapper.findByUserId(userId);
        if (stats == null) {
            // Create default stats if not exists
            stats = new UserStats();
            stats.setUserId(userId);
            stats.setLevel(1);
            stats.setCurrentXp(0);
            stats.setNextLevelXp(100);
            stats.setTotalFocusMinutes(0);
            stats.setStreakDays(0);
            userStatsMapper.insert(stats);
        }
        return stats;
    }

    public void addFocusTime(Long userId, Integer durationSeconds) {
        UserStats stats = getUserStats(userId);
        
        // 1. Update Focus Time
        int minutes = durationSeconds / 60;
        stats.setTotalFocusMinutes(stats.getTotalFocusMinutes() + minutes);

        // 2. Update Streak
        LocalDate today = LocalDate.now();
        if (stats.getLastFocusDate() == null) {
            stats.setStreakDays(1);
        } else if (stats.getLastFocusDate().equals(today.minusDays(1))) {
            stats.setStreakDays(stats.getStreakDays() + 1);
        } else if (!stats.getLastFocusDate().equals(today)) {
            // Reset streak if missed a day (and not today)
            stats.setStreakDays(1);
        }
        stats.setLastFocusDate(today);

        // 3. Update XP & Level
        // Rule: 1 minute = 1 XP
        int xpGained = minutes; 
        stats.setCurrentXp(stats.getCurrentXp() + xpGained);

        while (stats.getCurrentXp() >= stats.getNextLevelXp()) {
            stats.setCurrentXp(stats.getCurrentXp() - stats.getNextLevelXp());
            stats.setLevel(stats.getLevel() + 1);
            // Simple leveling curve: Next level needs 20% more XP
            stats.setNextLevelXp((int) (stats.getNextLevelXp() * 1.2));
        }

        userStatsMapper.update(stats);
    }
}
