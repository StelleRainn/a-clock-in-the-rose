package com.stellerainn.backend.mapper;

import com.stellerainn.backend.entity.UserStats;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserStatsMapper {

    @Select("SELECT * FROM user_stats WHERE user_id = #{userId}")
    UserStats findByUserId(Long userId);

    @Insert("INSERT INTO user_stats(user_id, level, current_xp, next_level_xp, total_focus_minutes, streak_days) " +
            "VALUES(#{userId}, #{level}, #{currentXp}, #{nextLevelXp}, #{totalFocusMinutes}, #{streakDays})")
    void insert(UserStats userStats);

    @Update("UPDATE user_stats SET level=#{level}, current_xp=#{currentXp}, next_level_xp=#{nextLevelXp}, " +
            "total_focus_minutes=#{totalFocusMinutes}, streak_days=#{streakDays}, last_focus_date=#{lastFocusDate} " +
            "WHERE user_id=#{userId}")
    void update(UserStats userStats);
}
