//package com.pn.career.services;
//
//import com.pn.career.models.ChatHistory;
//import com.pn.career.repositories.ChatHistoryRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ChatHistoryService {
//    private final ChatHistoryRepository chatHistoryRepository;
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    // Lưu tin nhắn mới
//    public ChatHistory saveChatHistory(ChatHistory chatHistory) {
//        if (chatHistory.getId() == null) {
//            chatHistory.setId(UUID.randomUUID().toString());
//        }
//        return chatHistoryRepository.save(chatHistory);
//    }
//
//    public List<ChatHistory> getChatHistoryBySessionId(String sessionId, int limit) {
//        List<ChatHistory> allMessages = chatHistoryRepository.findBySessionIdOrderByTimestampDesc(sessionId);
//        return allMessages.stream()
//                .limit(limit)
//                .collect(Collectors.toList());
//    }
//
//    public List<ChatHistory> getChatHistoryByUserId(String userId) {
//        return chatHistoryRepository.findByUserIdOrderByTimestampDesc(userId);
//    }
//
//    public void deleteChatHistoryBySessionId(String sessionId) {
//        chatHistoryRepository.deleteBySessionId(sessionId);
//    }
//
//    public List<String> analyzeFrequentQuestions(String userType, int limit) {
//        String key = "frequent_questions:" + userType;
//        return redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1)
//                .stream()
//                .map(Object::toString)
//                .collect(Collectors.toList());
//    }
//
//    public void incrementQuestionFrequency(String question, String userType) {
//        String key = "frequent_questions:" + userType;
//        redisTemplate.opsForZSet().incrementScore(key, question, 1);
//    }
//}
