package com.pn.career.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pn.career.models.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatbotController {
    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${open-api-key}")
    private String geminiApiKey;
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    @MessageMapping("/chatbot.send")
    public void processChatbotMessage(@Payload Map<String, Object> payload) {
        String sessionId = payload.containsKey("sessionId") ?
                payload.get("sessionId").toString() :
                UUID.randomUUID().toString();

        String content = payload.get("content").toString();
        log.info("Received chatbot message: sessionId={}, content={}", sessionId, content);

        String botResponse = getGeminiResponse(content);

        // Tạo phản hồi chatbot
        Message botMessage = new Message();
        botMessage.setContent(botResponse);

        // Thêm sessionId vào phản hồi để client có thể theo dõi
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("message", botMessage);

        // Gửi phản hồi từ chatbot về kênh công khai của phiên
        messagingTemplate.convertAndSend(
                "/chatbot/" + sessionId,
                response
        );
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
            String urlWithKey = GEMINI_API_URL + "?key=" + geminiApiKey;
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
                    Map<String, Object> content;
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
