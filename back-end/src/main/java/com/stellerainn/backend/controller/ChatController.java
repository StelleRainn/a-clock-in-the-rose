package com.stellerainn.backend.controller;

import com.stellerainn.backend.common.Result;
import com.stellerainn.backend.entity.ChatSession;
import com.stellerainn.backend.entity.ChatMessage;
import com.stellerainn.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // --- Sessions ---
    @GetMapping("/sessions")
    public Result<List<ChatSession>> getSessions(@RequestParam Long userId) {
        List<ChatSession> sessions = chatService.getSessionsByUserId(userId);
        return Result.success(sessions);
    }

    @PostMapping("/sessions")
    public Result<ChatSession> createSession(@RequestBody ChatSession request) {
        ChatSession session = chatService.createSession(request.getUserId(), request.getTitle());
        return Result.success(session);
    }

    @PutMapping("/sessions/{id}")
    public Result<Void> updateSessionTitle(@PathVariable Long id, @RequestBody ChatSession request) {
        chatService.updateSessionTitle(id, request.getTitle());
        return Result.success(null);
    }

    @DeleteMapping("/sessions/{id}")
    public Result<Void> deleteSession(@PathVariable Long id) {
        chatService.deleteSession(id);
        return Result.success(null);
    }

    // --- Messages ---
    @GetMapping("/sessions/{sessionId}/messages")
    public Result<List<ChatMessage>> getMessages(@PathVariable Long sessionId) {
        List<ChatMessage> messages = chatService.getMessagesBySessionId(sessionId);
        return Result.success(messages);
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public Result<ChatMessage> saveMessage(@PathVariable Long sessionId, @RequestBody ChatMessage request) {
        ChatMessage message = chatService.saveMessage(sessionId, request.getRole(), request.getContent());
        return Result.success(message);
    }
}
