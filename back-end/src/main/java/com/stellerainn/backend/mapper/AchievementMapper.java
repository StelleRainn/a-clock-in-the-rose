package com.stellerainn.backend.mapper;

import com.stellerainn.backend.entity.Achievement;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AchievementMapper {

    @Select("SELECT * FROM achievements")
    List<Achievement> findAll();

    @Select("SELECT a.*, ua.unlocked_at FROM achievements a " +
            "LEFT JOIN user_achievements ua ON a.id = ua.achievement_id AND ua.user_id = #{userId}")
    List<Achievement> findAllWithUserStatus(Long userId);

    @Select("SELECT * FROM achievements WHERE criteria_type = #{type}")
    List<Achievement> findByCriteriaType(String type);

    @Insert("INSERT IGNORE INTO user_achievements(user_id, achievement_id) VALUES(#{userId}, #{achievementId})")
    void unlockAchievement(Long userId, Long achievementId);
}
