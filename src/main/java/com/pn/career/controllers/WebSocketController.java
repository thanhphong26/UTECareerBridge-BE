package com.pn.career.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pn.career.models.Message;
import com.pn.career.models.User;
import com.pn.career.responses.MessageResponse;
import com.pn.career.services.IMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
@Controller
@Slf4j
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final IMessageService messageService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String GEMINI_API_KEY = "AIzaSyDXWlHmpCsFxgHnCq6XObe_OxwXn44EGFk";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    private static final Integer CHATBOT_ID = 999;
    @MessageMapping("/chat")
    public void processMessage(@Payload Map<String, Object> payload) {
        Integer senderId = Integer.parseInt(payload.get("senderId").toString());
        Integer recipientId = Integer.parseInt(payload.get("recipientId").toString());
        String content = payload.get("content").toString();
        log.info("Received message: sender={}, recipient={}, content={}",
                senderId, recipientId, content);


        if(!recipientId.equals(CHATBOT_ID)){
           MessageResponse savedMessage = messageService.sendMessage(senderId, recipientId, content);
           log.info("Sending message to user: {}", recipientId);

           messagingTemplate.convertAndSendToUser(
                   recipientId.toString(),
                   "/queue/messages",
                   savedMessage
           );
       }
        if (recipientId.equals(CHATBOT_ID)) {
            String botResponse = getGeminiResponse(content); // Gọi OpenAI

            // Tạo phản hồi chatbot mà không cần lưu vào DB
            Message botMessage = new Message();
            // User gửi tin nhắn
            botMessage.setContent(botResponse);

            // Gửi phản hồi từ chatbot về người dùng
            messagingTemplate.convertAndSendToUser(
                    senderId.toString(),
                    "/queue/messages",
                    botMessage
            );
        }
    }
    private String getGeminiResponse(String userMessage) {
        try {
            // Thiết lập header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Tạo body yêu cầu cho Gemini API
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", new Object[]{
                    new HashMap<String, Object>() {{
                        put("role", "user");
                        put("parts", new Object[]{
                                new HashMap<String, String>() {{
                                    put("text", userMessage);
                                }}
                        });
                    }}
            });

            // Sử dụng URL với query parameter key
            String urlWithKey = GEMINI_API_URL + "?key=" + GEMINI_API_KEY;
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Gửi yêu cầu và nhận String JSON
            String jsonResponse = restTemplate.postForObject(urlWithKey, entity, String.class);
            log.info("Gemini API Raw Response: {}", jsonResponse);

            // Parse JSON thủ công bằng ObjectMapper
            Map<String, Object> response = objectMapper.readValue(jsonResponse, Map.class);

            // Kiểm tra và lấy nội dung phản hồi
            if (response != null && response.containsKey("candidates")) {
                Object candidatesObj = response.get("candidates");
                List<Map<String, Object>> candidates;
                if (candidatesObj instanceof List) {
                    candidates = (List<Map<String, Object>>) candidatesObj;
                } else {
                    log.error("Unexpected candidates format: {}", candidatesObj);
                    return "Invalid response format from Gemini.";
                }

                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Object contentObj = candidate.get("content");
                    Map<String, Object> content; // Thay List bằng Map
                    if (contentObj instanceof Map) {
                        content = (Map<String, Object>) contentObj;
                    } else {
                        log.error("Unexpected content format: {}", contentObj);
                        return "Invalid response format from Gemini.";
                    }

                    if (content != null && content.containsKey("parts")) {
                        Object partsObj = content.get("parts");
                        List<Map<String, Object>> parts;
                        if (partsObj instanceof List) {
                            parts = (List<Map<String, Object>>) partsObj;
                        } else {
                            log.error("Unexpected parts format: {}", partsObj);
                            return "Invalid response format from Gemini.";
                        }

                        if (parts != null && !parts.isEmpty()) {
                            return (String) parts.get(0).get("text");
                        }
                    }
                }
            }
            return "No response from Gemini.";
        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage());
            log.error("Full error: {}", e.getStackTrace());
            return "Sorry, I couldn't process your request right now.";
        }
    }
}
