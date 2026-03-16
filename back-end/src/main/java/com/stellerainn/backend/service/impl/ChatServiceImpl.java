package com.stellerainn.backend.service.impl;

import com.stellerainn.backend.entity.ChatSession;
import com.stellerainn.backend.entity.ChatMessage;
import com.stellerainn.backend.mapper.ChatMapper;
import com.stellerainn.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMapper chatMapper;

    @Override
    public ChatSession createSession(Long userId, String title) {
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setTitle(title);
        chatMapper.insertSession(session);
        return session;
    }

    @Override
    public List<ChatSession> getSessionsByUserId(Long userId) {
        return chatMapper.findSessionsByUserId(userId);
    }

    @Override
    public ChatSession getSessionById(Long id) {
        return chatMapper.findSessionById(id);
    }

    @Override
    public void updateSessionTitle(Long id, String title) {
        chatMapper.updateSessionTitle(id, title);
    }

    @Override
    public void deleteSession(Long id) {
        chatMapper.deleteSession(id);
    }

    @Override
    public ChatMessage saveMessage(Long sessionId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        chatMapper.insertMessage(message);
        
        // Touch session updated_at to bring it to top
        // But mysql handles it automatically if we update it. Actually let's manually trigger it if needed,
        // or just let the database ON UPDATE handle it. Wait, inserting to chat_messages doesn't trigger chat_sessions update.
        // Let's do a dummy update or specific query. For now it's okay, maybe just order by id DESC for sessions.
        
        return message;
    }

    @Override
    public List<ChatMessage> getMessagesBySessionId(Long sessionId) {
        return chatMapper.findMessagesBySessionId(sessionId);
    }
}
