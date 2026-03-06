package com.stellerainn.backend.service;

import com.stellerainn.backend.entity.Achievement;
import com.stellerainn.backend.entity.UserStats;
import com.stellerainn.backend.mapper.AchievementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AchievementService {

    @Autowired
    private AchievementMapper achievementMapper;

    @Autowired
    private UserStatsService userStatsService;

    public List<Achievement> getUserAchievements(Long userId) {
        List<Achievement> list = achievementMapper.findAllWithUserStatus(userId);
        for (Achievement a : list) {
            a.setUnlocked(a.getUnlockedAt() != null);
        }
        return list;
    }

    public void checkAndUnlock(Long userId) {
        UserStats stats = userStatsService.getUserStats(userId);
        List<Achievement> achievements = achievementMapper.findAllWithUserStatus(userId);

        for (Achievement a : achievements) {
            if (a.getUnlockedAt() != null) continue; // Already unlocked

            boolean unlocked = false;
            switch (a.getCriteriaType()) {
                case "TOTAL_FOCUS_TIME":
                    if (stats.getTotalFocusMinutes() >= a.getCriteriaValue()) unlocked = true;
                    break;
                case "STREAK":
                    if (stats.getStreakDays() >= a.getCriteriaValue()) unlocked = true;
                    break;
                // Add more cases like TASK_COMPLETED
            }

            if (unlocked) {
                achievementMapper.unlockAchievement(userId, a.getId());
            }
        }
    }
}
