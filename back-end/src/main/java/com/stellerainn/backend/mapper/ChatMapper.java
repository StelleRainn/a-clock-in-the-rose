package com.stellerainn.backend.mapper;

import com.stellerainn.backend.entity.ChatSession;
import com.stellerainn.backend.entity.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMapper {

    // Chat Session operations
    @Insert("INSERT INTO chat_sessions(user_id, title) VALUES(#{userId}, #{title})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertSession(ChatSession session);

    @Select("SELECT * FROM chat_sessions WHERE user_id = #{userId} ORDER BY updated_at DESC")
    @Results({
        @Result(property = "userId", column = "user_id"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<ChatSession> findSessionsByUserId(Long userId);

    @Select("SELECT * FROM chat_sessions WHERE id = #{id}")
    @Results({
        @Result(property = "userId", column = "user_id"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    ChatSession findSessionById(Long id);

    @Update("UPDATE chat_sessions SET title = #{title} WHERE id = #{id}")
    void updateSessionTitle(@Param("id") Long id, @Param("title") String title);

    @Delete("DELETE FROM chat_sessions WHERE id = #{id}")
    void deleteSession(Long id);

    // Chat Message operations
    @Insert("INSERT INTO chat_messages(session_id, role, content) VALUES(#{sessionId}, #{role}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertMessage(ChatMessage message);

    @Select("SELECT * FROM chat_messages WHERE session_id = #{sessionId} ORDER BY created_at ASC")
    @Results({
        @Result(property = "sessionId", column = "session_id"),
        @Result(property = "createdAt", column = "created_at")
    })
    List<ChatMessage> findMessagesBySessionId(Long sessionId);
}
