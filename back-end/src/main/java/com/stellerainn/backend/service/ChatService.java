package com.stellerainn.backend.service;

import com.stellerainn.backend.entity.ChatSession;
import com.stellerainn.backend.entity.ChatMessage;
import java.util.List;

public interface ChatService {
    ChatSession createSession(Long userId, String title);
    List<ChatSession> getSessionsByUserId(Long userId);
    ChatSession getSessionById(Long id);
    void updateSessionTitle(Long id, String title);
    void deleteSession(Long id);
    
    ChatMessage saveMessage(Long sessionId, String role, String content);
    List<ChatMessage> getMessagesBySessionId(Long sessionId);
}
