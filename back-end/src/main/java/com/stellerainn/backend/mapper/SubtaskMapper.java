package com.stellerainn.backend.mapper;

import com.stellerainn.backend.entity.Subtask;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SubtaskMapper {

    @Select("SELECT * FROM subtasks WHERE task_id = #{taskId}")
    List<Subtask> findByTaskId(Long taskId);

    @Insert("INSERT INTO subtasks(task_id, title, completed) VALUES(#{taskId}, #{title}, #{completed})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Subtask subtask);

    @Update("UPDATE subtasks SET title=#{title}, completed=#{completed} WHERE id=#{id}")
    void update(Subtask subtask);

    @Delete("DELETE FROM subtasks WHERE id = #{id}")
    void delete(Long id);
}
