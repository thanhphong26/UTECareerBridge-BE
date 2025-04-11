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

import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatbotController {
    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${open-api-key}")
    private String geminiApiKey;
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models//gemini-2.0-flash:generateContent";

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

            // Tạo system prompt chứa thông tin về trang web
            String SYSTEM_PROMPT = "Đây là trợ lý AI của website tuyển dụng và hỗ trợ việc làm UTE Career. "
                    + "Nhiệm vụ của bạn là hỗ trợ người dùng với các thông tin chính xác và hữu ích về trang web và dịch vụ. "
                    + "Dưới đây là thông tin quan trọng về trang web:\n\n"
                    + "# Thông tin cơ bản\n"
                    + "- UTE Career là nền tảng kết nối sinh viên, cựu sinh viên trường Đại học Sư phạm Kỹ thuật TP.HCM với các nhà tuyển dụng\n"
                    + "- Website: utecareer.edu.vn\n"
                    + "- Hotline: 028.1234.5678\n"
                    + "- Email: support@utecareer.edu.vn\n"
                    + "- Địa chỉ: Phòng A1-805, số 1 Võ Văn Ngân, P. Linh Chiểu, TP. Thủ Đức, TP.HCM\n"
                    + "- Giờ làm việc: Thứ 2-6 từ 8h00-17h00, thứ 7 từ 8h00-12h00\n\n"
                    + "# Dịch vụ chính\n"
                    + "- Cho sinh viên/cựu sinh viên: tìm kiếm việc làm, tư vấn nghề nghiệp, hỗ trợ CV, đánh giá năng lực\n"
                    + "- Cho nhà tuyển dụng: đăng tin tuyển dụng, tìm kiếm ứng viên tiềm năng, tổ chức tuyển dụng tại trường\n\n"
                    + "# Sự kiện thường xuyên\n"
                    + "- Ngày hội việc làm UTE Job Fair: tổ chức 2 lần/năm vào tháng 4 và tháng 10\n"
                    + "- Workshop kỹ năng: tổ chức hàng tháng với các chủ đề về kỹ năng mềm và chuyên môn\n"
                    + "- Hội thảo doanh nghiệp: giới thiệu cơ hội thực tập và việc làm từ các doanh nghiệp đối tác\n\n"
                    + "# Quy trình đăng ký\n"
                    + "- Sinh viên/cựu sinh viên: đăng ký tài khoản trên website với email trường, hoàn thiện hồ sơ, tìm việc và ứng tuyển\n"
                    + "- Nhà tuyển dụng: đăng ký tài khoản doanh nghiệp, được xác minh bởi quản trị viên, đăng tin tuyển dụng\n\n"
                    + "Hướng dẫn cho chatbot:\n"
                    + "1. Trả lời thân thiện, ngắn gọn và chính xác\n"
                    + "2. Cung cấp thông tin hữu ích và liên quan để giải quyết vấn đề của người dùng\n"
                    + "3. Sử dụng ngôn ngữ phù hợp với đối tượng (sinh viên/doanh nghiệp)\n"
                    + "4. Đề xuất các bước tiếp theo nếu cần thiết\n"
                    + "5. Nếu không biết câu trả lời, đề nghị người dùng liên hệ qua email hoặc hotline\n"
                    + "6. Cung cấp thông tin liên hệ khi cần thiết";

            // Tạo body yêu cầu cho Gemini API với system prompt
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", new Object[]{
                    new HashMap<String, Object>() {{
                        put("role", "user");
                        put("parts", new Object[]{
                                new HashMap<String, String>() {{
                                    put("text", SYSTEM_PROMPT);
                                }}
                        });
                    }},
                    new HashMap<String, Object>() {{
                        put("role", "user");
                        put("parts", new Object[]{
                                new HashMap<String, String>() {{
                                    put("text", userMessage);
                                }}
                        });
                    }}
            });

            // Thêm các tùy chọn để kiểm soát đầu ra
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.2); // Giảm temperature để câu trả lời nhất quán
            generationConfig.put("maxOutputTokens", 500); // Giới hạn độ dài câu trả lời
            requestBody.put("generationConfig", generationConfig);

            List<Map<String, Object>> safetySettings = new ArrayList<>();
            safetySettings.add(Map.of(
                    "category", "HARM_CATEGORY_HARASSMENT",
                    "threshold", "BLOCK_MEDIUM_AND_ABOVE"
            ));
            safetySettings.add(Map.of(
                    "category", "HARM_CATEGORY_HATE_SPEECH",
                    "threshold", "BLOCK_MEDIUM_AND_ABOVE"
            ));
            requestBody.put("safetySettings", safetySettings);

            String urlWithKey = GEMINI_API_URL + "?key=" + geminiApiKey;
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String jsonResponse = restTemplate.postForObject(urlWithKey, entity, String.class);

            Map<String, Object> response = objectMapper.readValue(jsonResponse, Map.class);

            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                    if (content != null && content.containsKey("parts")) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        if (parts != null && !parts.isEmpty()) {
                            return (String) parts.get(0).get("text");
                        }
                    }
                }
            }
            return "Xin lỗi, tôi không thể xử lý yêu cầu của bạn vào lúc này.";
        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage());
            return "Xin lỗi, đã xảy ra lỗi khi xử lý yêu cầu của bạn.";
        }
    }

}
