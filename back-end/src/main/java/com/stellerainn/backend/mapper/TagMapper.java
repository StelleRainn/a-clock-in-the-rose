package com.stellerainn.backend.mapper;

import com.stellerainn.backend.entity.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TagMapper {

    @Select("SELECT * FROM tags WHERE user_id = #{userId}")
    List<Tag> findByUserId(Long userId);

    @Insert("INSERT INTO tags(user_id, name, color) VALUES(#{userId}, #{name}, #{color})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Tag tag);

    @Delete("DELETE FROM tags WHERE id = #{id} AND user_id = #{userId}")
    void delete(Long id, Long userId);

    // Task-Tag Relations
    @Insert("INSERT IGNORE INTO task_tags(task_id, tag_id) VALUES(#{taskId}, #{tagId})")
    void addTaskTag(Long taskId, Long tagId);

    @Delete("DELETE FROM task_tags WHERE task_id = #{taskId}")
    void removeAllTaskTags(Long taskId);

    @Select("SELECT t.* FROM tags t JOIN task_tags tt ON t.id = tt.tag_id WHERE tt.task_id = #{taskId}")
    List<Tag> findTagsByTaskId(Long taskId);
}
