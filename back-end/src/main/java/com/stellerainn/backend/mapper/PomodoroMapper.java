package com.stellerainn.backend.mapper;

import com.stellerainn.backend.entity.DailyStats;
import com.stellerainn.backend.entity.PomodoroRecord;
import com.stellerainn.backend.entity.TagFocusStats;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PomodoroMapper {

    @Insert("INSERT INTO pomodoro_records(user_id, task_id, start_time, end_time, duration_seconds, status) " +
            "VALUES(#{userId}, #{taskId}, #{startTime}, #{endTime}, #{durationSeconds}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PomodoroRecord record);

    @Select("SELECT * FROM pomodoro_records WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<PomodoroRecord> findByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM pomodoro_records WHERE user_id = #{userId} AND status = 'COMPLETED' AND DATE(created_at) = CURDATE()")
    Integer countTodayCompleted(Long userId);

    @Select("SELECT COALESCE(SUM(duration_seconds), 0) FROM pomodoro_records WHERE user_id = #{userId} AND status = 'COMPLETED' AND DATE(created_at) = CURDATE()")
    Long sumTodayDurationSeconds(Long userId);

    @Select("SELECT DATE(start_time) as date, SUM(duration_seconds) as totalSeconds " +
            "FROM pomodoro_records " +
            "WHERE user_id = #{userId} AND status = 'COMPLETED' " +
            "AND start_time >= DATE_SUB(CURDATE(), INTERVAL 365 DAY) " +
            "GROUP BY DATE(start_time) " +
            "ORDER BY date ASC")
    List<DailyStats> getDailyFocusTime(Long userId);

    @Select("SELECT t.name as tagName, t.color as tagColor, SUM(pr.duration_seconds) as totalSeconds " +
            "FROM pomodoro_records pr " +
            "JOIN task_tags tt ON pr.task_id = tt.task_id " +
            "JOIN tags t ON tt.tag_id = t.id " +
            "WHERE pr.user_id = #{userId} AND pr.status = 'COMPLETED' " +
            "GROUP BY t.id, t.name, t.color " +
            "ORDER BY totalSeconds DESC")
    List<TagFocusStats> getTagFocusStats(Long userId);
}
