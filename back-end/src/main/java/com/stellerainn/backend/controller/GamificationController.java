package com.stellerainn.backend.controller;

import com.stellerainn.backend.common.Result;
import com.stellerainn.backend.entity.Achievement;
import com.stellerainn.backend.entity.UserStats;
import com.stellerainn.backend.service.AchievementService;
import com.stellerainn.backend.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gamification")
public class GamificationController {

    @Autowired
    private UserStatsService userStatsService;

    @Autowired
    private AchievementService achievementService;

    @GetMapping("/stats")
    public Result<UserStats> getUserStats(@RequestParam Long userId) {
        return Result.success(userStatsService.getUserStats(userId));
    }

    @GetMapping("/achievements")
    public Result<List<Achievement>> getUserAchievements(@RequestParam Long userId) {
        // Trigger a check before returning (lazy evaluation)
        achievementService.checkAndUnlock(userId);
        return Result.success(achievementService.getUserAchievements(userId));
    }
}
