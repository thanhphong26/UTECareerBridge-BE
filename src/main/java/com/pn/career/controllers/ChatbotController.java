package com.pn.career.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pn.career.models.*;
import com.pn.career.responses.EmployerResponse;
import com.pn.career.responses.EventResponse;
import com.pn.career.responses.JobResponse;
import com.pn.career.services.*;
import com.pn.career.utils.LanguageDetector;
import jakarta.transaction.Transactional;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatbotController {
    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final IJobSkillService jobSkillService;
    private final IJobService jobService;
    private final IEmployerService employerService;
    private final IEventService eventService;
    private final IJobCategoryService categoryService;
    private final LanguageDetector languageDetector;

    private final Map<String, List<Map<String, Object>>> sessionHistories = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> userPreferences = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> sessionLastActivity = new ConcurrentHashMap<>();

    private static final String LINK_MARKUP = "[[LINK]]";
    private static final String RICH_MARKUP = "[[RICH]]";

    @Value("${open-api-key}")
    private String geminiApiKey;
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    @Value("${application.url:https://utecareer.edu.vn}")
    private String applicationUrl;

    private static final String SYSTEM_PROMPT_VI = "Đây là trợ lý AI của website tuyển dụng và hỗ trợ việc làm UTE Career. "
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
            + "6. Cung cấp thông tin liên hệ khi cần thiết\n"
            + "7. Khi người dùng hỏi về số liệu thực tế (số lượng công ty, việc làm), hãy sử dụng dữ liệu được cung cấp trong tin nhắn\n"
            + "8. Khi người dùng yêu cầu đề xuất việc làm phù hợp, hãy dựa vào thông tin ưa thích/kỹ năng của họ và danh sách việc làm được cung cấp\n"
            + "9. Để thêm liên kết vào văn bản phản hồi, hãy sử dụng cú pháp sau: " + LINK_MARKUP + "{text}|{url}" + LINK_MARKUP + "\n"
            + "10. Để thêm nội dung có định dạng phong phú như danh sách việc làm, sự kiện, công ty, hãy sử dụng cú pháp: " + RICH_MARKUP + "{type}|{id}" + RICH_MARKUP + "\n"
            + "Ví dụ: " + RICH_MARKUP + "job|123" + RICH_MARKUP + " để hiển thị thông tin chi tiết về việc làm có ID 123";

    private static final String SYSTEM_PROMPT_EN = "You are an AI assistant for the UTE Career job recruitment and employment support website. "
            + "Your mission is to provide users with accurate and helpful information about the website and services. "
            + "Here is important information about the website:\n\n"
            + "# Basic Information\n"
            + "- UTE Career is a platform connecting students and alumni of HCMC University of Technology and Education with employers\n"
            + "- Website: utecareer.edu.vn\n"
            + "- Hotline: 028.1234.5678\n"
            + "- Email: support@utecareer.edu.vn\n"
            + "- Address: Room A1-805, 1 Vo Van Ngan Street, Linh Chieu Ward, Thu Duc City, HCMC\n"
            + "- Working hours: Monday-Friday 8:00-17:00, Saturday 8:00-12:00\n\n"
            + "# Main Services\n"
            + "- For students/alumni: job search, career counseling, CV support, skill assessment\n"
            + "- For employers: posting job listings, searching for potential candidates, organizing on-campus recruitment\n\n"
            + "# Regular Events\n"
            + "- UTE Job Fair: held twice a year in April and October\n"
            + "- Skills Workshops: monthly events on soft skills and professional topics\n"
            + "- Company Seminars: introducing internship and job opportunities from partner companies\n\n"
            + "# Registration Process\n"
            + "- Students/alumni: register an account on the website with school email, complete profile, search and apply for jobs\n"
            + "- Employers: register a company account, verified by administrators, post job listings\n\n"
            + "Guidelines for the chatbot:\n"
            + "1. Respond in a friendly, concise, and accurate manner\n"
            + "2. Provide useful and relevant information to solve the user's issues\n"
            + "3. Use appropriate language for the audience (students/companies)\n"
            + "4. Suggest next steps if necessary\n"
            + "5. If you don't know the answer, suggest the user contact via email or hotline\n"
            + "6. Provide contact information when necessary\n"
            + "7. When users ask about actual statistics (number of companies, jobs), use the data provided in the message\n"
            + "8. When users request job recommendations, base them on their preferences/skills and the job list provided\n"
            + "9. To add links to your response text, use the following syntax: " + LINK_MARKUP + "{text}|{url}" + LINK_MARKUP + "\n"
            + "10. To add rich formatted content like job listings, events, companies, use the syntax: " + RICH_MARKUP + "{type}|{id}" + RICH_MARKUP + "\n"
            + "Example: " + RICH_MARKUP + "job|123" + RICH_MARKUP + " to display detailed information about job with ID 123";

    private void cleanUpExpiredSessions() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);

        for (Iterator<Map.Entry<String, LocalDateTime>> it = sessionLastActivity.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, LocalDateTime> entry = it.next();
            if (entry.getValue().isBefore(thirtyMinutesAgo)) {
                String sessionId = entry.getKey();
                sessionHistories.remove(sessionId);
                userPreferences.remove(sessionId);
                it.remove();
                log.info("Cleaned up expired session: {}", sessionId);
            }
        }
    }

    @MessageMapping("/chatbot.send")
    public void processChatbotMessage(@Payload Map<String, Object> payload) {
        cleanUpExpiredSessions();

        String sessionId = payload.containsKey("sessionId") ?
                payload.get("sessionId").toString() :
                UUID.randomUUID().toString();

        sessionLastActivity.put(sessionId, LocalDateTime.now());

        String content = payload.get("content").toString();
        log.info("Received chatbot message: sessionId={}, content={}", sessionId, content);

        String detectedLanguage = languageDetector.detectLanguage(content);

        if (processSpecialCommands(sessionId, content, detectedLanguage)) {
            return;
        }

        addMessageToSessionHistory(sessionId, "user", content);

        Map<String, Object> realTimeData = collectRealTimeData(sessionId, content);

        String botResponse = getGeminiResponse(content, sessionId, detectedLanguage, realTimeData);

        Map<String, Object> processedResponse = processResponseMarkups(botResponse, detectedLanguage);
        String processedText = (String) processedResponse.get("text");
        List<Map<String, Object>> richElements = (List<Map<String, Object>>) processedResponse.get("richElements");

        addMessageToSessionHistory(sessionId, "assistant", processedText);

        Message botMessage = new Message();
        botMessage.setContent(processedText);

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("message", botMessage);
        response.put("language", detectedLanguage);
        response.put("richElements", richElements);

        if (userPreferences.containsKey(sessionId)) {
            response.put("userPreferences", userPreferences.get(sessionId));
        }

        messagingTemplate.convertAndSend(
                "/chatbot/" + sessionId,
                response
        );
    }

    private Map<String, Object> processResponseMarkups(String response, String language) {
        String processedText = response;
        List<Map<String, Object>> richElements = new ArrayList<>();

        log.debug("Processing response text: {}", response);

        // Process link markups
        while (processedText.contains(LINK_MARKUP)) {
            int startIndex = processedText.indexOf(LINK_MARKUP);
            int endIndex = processedText.indexOf(LINK_MARKUP, startIndex + LINK_MARKUP.length());

            if (endIndex != -1) {
                String linkData = processedText.substring(startIndex + LINK_MARKUP.length(), endIndex);
                log.debug("Processing link markup: {}", linkData);

                String[] parts = linkData.split("\\|");

                if (parts.length == 2) {
                    String text = parts[0].trim();
                    String url = parts[1].trim();

                    // Fix common formatting issues
                    if (text.startsWith("{")) text = text.substring(1);
                    if (text.endsWith("}")) text = text.substring(0, text.length() - 1);
                    if (url.endsWith("}")) url = url.substring(0, url.length() - 1);

                    // Ensure URL has proper protocol
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "https://" + url;
                    }

                    log.debug("Link parts: text='{}', url='{}'", text, url);

                    // Create proper HTML link
                    String replacement = "<a href=\"" + url + "\" target=\"_blank\">" + text + "</a>";
                    log.debug("Replacement HTML: {}", replacement);

                    processedText = processedText.substring(0, startIndex) + replacement +
                            processedText.substring(endIndex + LINK_MARKUP.length());
                } else {
                    // If format is incorrect, just remove the markup
                    log.debug("Invalid link format: parts length = {}", parts.length);
                    processedText = processedText.substring(0, startIndex) +
                            processedText.substring(endIndex + LINK_MARKUP.length());
                }
            } else {
                break;
            }
        }

        // Process rich content markups (same logic as before)
        while (processedText.contains(RICH_MARKUP)) {
            int startIndex = processedText.indexOf(RICH_MARKUP);
            int endIndex = processedText.indexOf(RICH_MARKUP, startIndex + RICH_MARKUP.length());

            if (endIndex != -1) {
                String richData = processedText.substring(startIndex + RICH_MARKUP.length(), endIndex);
                String[] parts = richData.split("\\|");

                if (parts.length == 2) {
                    String type = parts[0].trim();
                    String id = parts[1].trim();

                    // Remove any trailing curly braces
                    if (id.endsWith("}")) id = id.substring(0, id.length() - 1);

                    Map<String, Object> richElement = createRichElement(type, id, language);
                    richElements.add(richElement);

                    processedText = processedText.substring(0, startIndex) +
                            processedText.substring(endIndex + RICH_MARKUP.length());
                } else {
                    processedText = processedText.substring(0, startIndex) +
                            processedText.substring(endIndex + RICH_MARKUP.length());
                }
            } else {
                break;
            }
        }

        log.debug("Final processed text: {}", processedText);

        Map<String, Object> result = new HashMap<>();
        result.put("text", processedText);
        result.put("richElements", richElements);
        return result;
    }

    private Map<String, Object> createRichElement(String type, String id, String language) {
        Map<String, Object> richElement = new HashMap<>();
        richElement.put("type", type);
        richElement.put("id", id);

        try {
            switch (type.toLowerCase()) {
                case "job":
                    JobResponse job = jobService.getJobById(Integer.valueOf(id), JobStatus.ACTIVE)
                            .orElseThrow(() -> new Exception("Job not found"));
                    Map<String, Object> jobData = new HashMap<>();
                    jobData.put("title", job.getJobTitle());
                    jobData.put("company", job.getEmployerResponse().getCompanyName());
                    jobData.put("salary", job.getJobMinSalary() + " - " + job.getJobMaxSalary());
                    jobData.put("location", job.getJobLocation());
                    jobData.put("skills", job.getJobSkills().stream()
                            .map(skill -> skill.getSkillName())
                            .collect(Collectors.joining(", ")));
                    jobData.put("description", job.getJobDescription());
                    jobData.put("postDate", job.getCreatedAt().format(DateTimeFormatter.ISO_DATE));
                    jobData.put("applyUrl", applicationUrl + "/jobs/" + id + "/apply");
                    jobData.put("viewUrl", applicationUrl + "/jobs/" + id);
                    richElement.put("data", jobData);
                    break;

                case "employer":
                    Employer employer = employerService.getEmployerById(Integer.valueOf(id));
                    Map<String, Object> employerData = new HashMap<>();
                    employerData.put("name", employer.getCompanyName());
                    employerData.put("industry", employer.getIndustry());
                    employerData.put("location", employer.getCompanyAddress());
                    employerData.put("size", employer.getCompanySize());
                    employerData.put("description", employer.getCompanyDescription());
                    employerData.put("website", employer.getCompanyWebsite());
                    employerData.put("jobCount", jobService.countJobByEmployerIdAndStatus(Integer.valueOf(id), JobStatus.ACTIVE));
                    employerData.put("viewUrl", applicationUrl + "/employers/" + id);
                    richElement.put("data", employerData);
                    break;

                case "event":
                    EventResponse event = eventService.getEventById(Integer.valueOf(id));
                    Map<String, Object> eventData = new HashMap<>();
                    eventData.put("title", event.getEventTitle());
                    eventData.put("description", event.getEventDescription());
                    eventData.put("date", event.getEventDate().format(DateTimeFormatter.ISO_DATE));
                    eventData.put("location", event.getEventLocation());
                    eventData.put("registrationUrl", applicationUrl + "/events/" + id + "/register");
                    eventData.put("viewUrl", applicationUrl + "/events/" + id);
                    richElement.put("data", eventData);
                    break;

                case "category":
                    JobCategory category = categoryService.getJobCategoryById(Integer.valueOf(id));
                    Map<String, Object> categoryData = new HashMap<>();
                    categoryData.put("name", category.getJobCategoryName());
                    categoryData.put("jobCount", jobService.countJobByJobCategoryIdAndStatus(Integer.valueOf(id), JobStatus.ACTIVE));
                    categoryData.put("viewUrl", applicationUrl + "/jobs/category/" + id);
                    richElement.put("data", categoryData);
                    break;
                default:
                    richElement.put("data", new HashMap<>());
                    break;
            }
        } catch (Exception e) {
            log.error("Error creating rich element: {}", e.getMessage());
            richElement.put("error", e.getMessage());
            richElement.put("data", new HashMap<>());
        }

        return richElement;
    }

    private boolean processSpecialCommands(String sessionId, String content, String language) {
        if (!userPreferences.containsKey(sessionId)) {
            userPreferences.put(sessionId, new HashMap<>());
            userPreferences.get(sessionId).put("language", language);
        }

        Map<String, Object> preferences = userPreferences.get(sessionId);

        if (content.toLowerCase().contains("kỹ năng của tôi là") ||
                content.toLowerCase().contains("my skills are")) {

            String skills = extractSkillsFromMessage(content);
            preferences.put("skills", skills);

            String response = language.equals("vi") ?
                    "Tôi đã ghi nhận kỹ năng của bạn: <b>" + skills + "</b>. Tôi sẽ sử dụng thông tin này để đề xuất việc làm phù hợp hơn." :
                    "I've noted your skills: <b>" + skills + "</b>. I'll use this information to provide more suitable job recommendations.";

            Message botMessage = new Message();
            botMessage.setContent(response);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("sessionId", sessionId);
            responseMap.put("message", botMessage);
            responseMap.put("language", language);
            responseMap.put("userPreferences", preferences);

            messagingTemplate.convertAndSend("/chatbot/" + sessionId, responseMap);
            return true;
        }

        if (content.toLowerCase().contains("tôi ở") ||
                content.toLowerCase().contains("i am located in") ||
                content.toLowerCase().contains("i live in")) {

            String location = extractLocationFromMessage(content, language);
            preferences.put("location", location);

            String response = language.equals("vi") ?
                    "Tôi đã ghi nhận vị trí của bạn: <b>" + location + "</b>. Tôi sẽ ưu tiên đề xuất việc làm gần khu vực này." :
                    "I've noted your location: <b>" + location + "</b>. I'll prioritize job recommendations near this area.";

            Message botMessage = new Message();
            botMessage.setContent(response);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("sessionId", sessionId);
            responseMap.put("message", botMessage);
            responseMap.put("language", language);
            responseMap.put("userPreferences", preferences);

            messagingTemplate.convertAndSend("/chatbot/" + sessionId, responseMap);
            return true;
        }

        if (content.toLowerCase().contains("tôi quan tâm đến ngành") ||
                content.toLowerCase().contains("tôi muốn làm trong lĩnh vực") ||
                content.toLowerCase().contains("i'm interested in") ||
                content.toLowerCase().contains("i want to work in")) {

            String field = extractFieldFromMessage(content, language);
            preferences.put("field", field);

            List<JobCategory> suggestedCategories = categoryService.getJobCategoryByName(field);
            List<Map<String, Object>> categoriesData = new ArrayList<>();

            for (JobCategory category : suggestedCategories) {
                Map<String, Object> categoryData = new HashMap<>();
                categoryData.put("id", category.getJobCategoryId());
                categoryData.put("name", category.getJobCategoryName());
                categoryData.put("jobCount", jobService.countJobByJobCategoryIdAndStatus(category.getJobCategoryId(), JobStatus.ACTIVE));
                categoriesData.add(categoryData);
            }

            String response = language.equals("vi") ?
                    "Tôi đã ghi nhận lĩnh vực bạn quan tâm: <b>" + field + "</b>." :
                    "I've noted your field of interest: <b>" + field + "</b>.";

            if (!suggestedCategories.isEmpty()) {
                response += language.equals("vi") ?
                        " Tôi tìm thấy một số ngành nghề phù hợp, bạn có thể xem bên dưới." :
                        " I found some matching categories, you can view them below.";
            }

            Message botMessage = new Message();
            botMessage.setContent(response);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("sessionId", sessionId);
            responseMap.put("message", botMessage);
            responseMap.put("language", language);
            responseMap.put("userPreferences", preferences);
            responseMap.put("suggestedCategories", categoriesData);

            messagingTemplate.convertAndSend("/chatbot/" + sessionId, responseMap);
            return true;
        }

        if (content.toLowerCase().contains("mức lương mong muốn") ||
                content.toLowerCase().contains("tôi muốn mức lương") ||
                content.toLowerCase().contains("expected salary") ||
                content.toLowerCase().contains("salary expectation")) {

            Map<String, Object> salaryInfo = extractSalaryFromMessage(content, language);
            preferences.put("minSalary", salaryInfo.get("min"));
            preferences.put("maxSalary", salaryInfo.get("max"));
            preferences.put("currency", salaryInfo.get("currency"));

            String salaryRange = salaryInfo.get("currency") + " " + salaryInfo.get("min") +
                    " - " + salaryInfo.get("currency") + " " + salaryInfo.get("max");

            String response = language.equals("vi") ?
                    "Tôi đã ghi nhận mức lương mong muốn của bạn: <b>" + salaryRange + "</b>. Tôi sẽ ưu tiên đề xuất việc làm với mức lương phù hợp." :
                    "I've noted your expected salary: <b>" + salaryRange + "</b>. I'll prioritize job recommendations with matching salary ranges.";

            Message botMessage = new Message();
            botMessage.setContent(response);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("sessionId", sessionId);
            responseMap.put("message", botMessage);
            responseMap.put("language", language);
            responseMap.put("userPreferences", preferences);
            messagingTemplate.convertAndSend("/chatbot/" + sessionId, responseMap);
            return true;
        }

        return false;
    }

    private String extractSharedContentFromMessage(String content, String language) {
        if (language.equals("vi")) {
            if (content.toLowerCase().contains("chia sẻ về")) {
                return content.toLowerCase().split("chia sẻ về")[1].trim();
            } else if (content.toLowerCase().contains("gửi cho bạn bè về")) {
                return content.toLowerCase().split("gửi cho bạn bè về")[1].trim();
            }
        } else {
            if (content.toLowerCase().contains("share about")) {
                return content.toLowerCase().split("share about")[1].trim();
            } else if (content.toLowerCase().contains("send to friends about")) {
                return content.toLowerCase().split("send to friends about")[1].trim();
            }
        }
        return content;
    }

    private String extractEventTitleFromMessage(String content, String language) {
        if (language.equals("vi")) {
            if (content.toLowerCase().contains("nhắc nhở về sự kiện")) {
                return content.toLowerCase().split("nhắc nhở về sự kiện")[1].trim();
            } else if (content.toLowerCase().contains("đăng ký nhắc nhở sự kiện")) {
                return content.toLowerCase().split("đăng ký nhắc nhở sự kiện")[1].trim();
            }
        } else {
            if (content.toLowerCase().contains("remind me about event")) {
                return content.toLowerCase().split("remind me about event")[1].trim();
            } else if (content.toLowerCase().contains("event reminder for")) {
                return content.toLowerCase().split("event reminder for")[1].trim();
            }
        }
        return "";
    }

    private Map<String, Object> extractSalaryFromMessage(String content, String language) {
        Map<String, Object> result = new HashMap<>();
        result.put("min", 5000000);
        result.put("max", 15000000);
        result.put("currency", "VND");

        try {
            List<Integer> numbers = new ArrayList<>();
            String[] words = content.split("\\s+");

            for (String word : words) {
                word = word.replaceAll("[^0-9]", "");
                if (!word.isEmpty()) {
                    try {
                        numbers.add(Integer.parseInt(word));
                    } catch (NumberFormatException ignored) {}
                }
            }

            String currency = "VND";
            if (content.toLowerCase().contains("usd") ||
                    content.toLowerCase().contains("dollar") ||
                    content.toLowerCase().contains("$")) {
                currency = "USD";
            }

            if (numbers.size() >= 2) {
                Collections.sort(numbers);
                result.put("min", numbers.get(0));
                result.put("max", numbers.get(numbers.size() - 1));
            } else if (numbers.size() == 1) {
                int value = numbers.get(0);
                result.put("min", value * 0.8);
                result.put("max", value * 1.2);
            }

            result.put("currency", currency);
        } catch (Exception e) {
            log.error("Error extracting salary: {}", e.getMessage());
        }

        return result;
    }

    private String extractFieldFromMessage(String content, String language) {
        if (language.equals("vi")) {
            if (content.toLowerCase().contains("tôi quan tâm đến ngành")) {
                return content.toLowerCase().split("tôi quan tâm đến ngành")[1].trim();
            } else if (content.toLowerCase().contains("tôi muốn làm trong lĩnh vực")) {
                return content.toLowerCase().split("tôi muốn làm trong lĩnh vực")[1].trim();
            }
        } else {
            if (content.toLowerCase().contains("i'm interested in")) {
                return content.toLowerCase().split("i'm interested in")[1].trim();
            } else if (content.toLowerCase().contains("i want to work in")) {
                return content.toLowerCase().split("i want to work in")[1].trim();
            }
        }
        return "";
    }

    private String extractLocationFromMessage(String content, String language) {
        if (language.equals("vi")) {
            if (content.toLowerCase().contains("tôi ở")) {
                return content.toLowerCase().split("tôi ở")[1].trim();
            }
        } else {
            if (content.toLowerCase().contains("i am located in")) {
                return content.toLowerCase().split("i am located in")[1].trim();
            } else if (content.toLowerCase().contains("i live in")) {
                return content.toLowerCase().split("i live in")[1].trim();
            }
        }
        return "";
    }

    private String extractSkillsFromMessage(String content) {
        if (content.toLowerCase().contains("kỹ năng của tôi là")) {
            return content.toLowerCase().split("kỹ năng của tôi là")[1].trim();
        } else if (content.toLowerCase().contains("my skills are")) {
            return content.toLowerCase().split("my skills are")[1].trim();
        }
        return "";
    }
    private Map<String, Object> collectRealTimeData(String sessionId, String content) {
        Map<String, Object> data = new HashMap<>();

        data.put("totalCompanies", employerService.countEmployerByStatus(EmployerStatus.APPROVED));
        data.put("totalJobs", jobService.countJobByActiveStatus(JobStatus.ACTIVE));
        data.put("totalCategories", categoryService.countJobCategory(true));
        data.put("totalEvents", eventService.countEventUpcomming(LocalDateTime.now()));

        Map<String, Object> userPref = userPreferences.getOrDefault(sessionId, new HashMap<>());

        if (content.toLowerCase().contains("sự kiện") ||
                content.toLowerCase().contains("hội thảo") ||
                content.toLowerCase().contains("event") ||
                content.toLowerCase().contains("workshop")) {

            List<EventResponse> upcomingEvents = eventService.getAllEventUpcomming(LocalDateTime.now(), 3);
            List<Map<String, Object>> formattedEvents = upcomingEvents.stream()
                    .map(event -> {
                        Map<String, Object> eventData = new HashMap<>();
                        eventData.put("id", event.getEventId());
                        eventData.put("title", event.getEventTitle());
                        eventData.put("date", event.getEventDate().format(DateTimeFormatter.ISO_DATE));
                        eventData.put("location", event.getEventLocation());
                        return eventData;
                    })
                    .collect(Collectors.toList());

            data.put("upcomingEvents", formattedEvents);
        }

        if (content.toLowerCase().contains("công ty") ||
                content.toLowerCase().contains("doanh nghiệp") ||
                content.toLowerCase().contains("company") ||
                content.toLowerCase().contains("employer")) {

            String companyKeyword = extractCompanyKeywordFromMessage(content);
            if (!companyKeyword.isEmpty()) {
                List<EmployerResponse> matchingEmployers = employerService.getAllEmployerByJobCategoryAndStatus(companyKeyword, EmployerStatus.APPROVED);
                List<Map<String, Object>> formattedEmployers = matchingEmployers.stream()
                        .map(employer -> {
                            Map<String, Object> employerData = new HashMap<>();
                            employerData.put("id", employer.getId());
                            employerData.put("name", employer.getCompanyName());
                            employerData.put("industry", employer.getIndustry());
                            employerData.put("location", employer.getCompanyAddress());
                            employerData.put("size", employer.getCompanySize());
                            employerData.put("website", employer.getCompanyWebsite());
                            return employerData;
                        })
                        .collect(Collectors.toList());

                data.put("matchingEmployers", formattedEmployers);
            }
        }

        if (content.toLowerCase().contains("xu hướng") ||
                content.toLowerCase().contains("ngành hot") ||
                content.toLowerCase().contains("trend") ||
                content.toLowerCase().contains("popular")) {

            //List<Map<String, Object>> trendingCategories = categoryService.getTrendingCategories(5);
          //  data.put("trendingCategories", trendingCategories);
        }

        if (content.toLowerCase().contains("đề xuất") ||
                content.toLowerCase().contains("gợi ý") ||
                content.toLowerCase().contains("recommend") ||
                content.toLowerCase().contains("suggest") ||
                content.toLowerCase().contains("tìm việc") ||
                content.toLowerCase().contains("find job")) {

            String skills = (String) userPref.getOrDefault("skills", "");
            String location = (String) userPref.getOrDefault("location", "");
            String field = (String) userPref.getOrDefault("field", "");

            List<Job> recommendedJobs = jobSkillService.getRecommendedJobs(skills, 5);
            log.info("Recommended jobs based on skills: {}", recommendedJobs);
            List<Map<String, Object>> formattedJobs = recommendedJobs.stream()
                    .map(job -> {
                        Map<String, Object> jobData = new HashMap<>();
                        jobData.put("id", job.getJobId());
                        jobData.put("title", job.getJobTitle());
                        jobData.put("company", job.getEmployer().getCompanyName());
                        jobData.put("companyId", job.getEmployer().getUserId());
                        jobData.put("salary", job.getJobMinSalary() + " - " + job.getJobMaxSalary());
                        jobData.put("location", job.getJobLocation());
                        jobData.put("skills", job.getJobSkills().stream()
                                .map(skill -> skill.getSkill().getSkillName())
                                .collect(Collectors.joining(", ")));
                        jobData.put("viewUrl", applicationUrl + "/jobs/" + job.getJobId());
                        return jobData;
                    })
                    .collect(Collectors.toList());

            data.put("recommendedJobs", formattedJobs);
            data.put("userPreferences", userPref);
        }

        return data;
    }

    private String extractCompanyKeywordFromMessage(String content) {
        String[] patterns = {
                "công ty (.*?)(?:\\s|$)",
                "doanh nghiệp (.*?)(?:\\s|$)",
                "company (.*?)(?:\\s|$)",
                "employer (.*?)(?:\\s|$)"
        };

        for (String pattern : patterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(content.toLowerCase());
            if (m.find()) {
                return m.group(1).trim();
            }
        }

        return "";
    }

    private void addMessageToSessionHistory(String sessionId, String role, String content) {
        if (!sessionHistories.containsKey(sessionId)) {
            sessionHistories.put(sessionId, new ArrayList<>());
        }

        Map<String, Object> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        message.put("timestamp", LocalDateTime.now().toString());

        List<Map<String, Object>> history = sessionHistories.get(sessionId);
        history.add(message);

        if (history.size() > 15) {
            history.remove(0);
        }
    }

    private String getGeminiResponse(String userMessage, String sessionId, String language, Map<String, Object> realTimeData) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String systemPrompt = language.equals("vi") ? SYSTEM_PROMPT_VI : SYSTEM_PROMPT_EN;

            String enhancedPrompt = systemPrompt + "\n\n";

            enhancedPrompt += language.equals("vi") ?
                    "# Dữ liệu hệ thống hiện tại:\n" :
                    "# Current system data:\n";

            enhancedPrompt += language.equals("vi") ?
                    "- Số lượng công ty đang hoạt động: " + realTimeData.get("totalCompanies") + "\n" +
                            "- Số lượng việc làm đang tuyển: " + realTimeData.get("totalJobs") + "\n" +
                            "- Số lượng ngành nghề: " + realTimeData.get("totalCategories") + "\n" +
                            "- Số lượng sự kiện sắp tới: " + realTimeData.get("totalEvents") + "\n" :
                    "- Number of active companies: " + realTimeData.get("totalCompanies") + "\n" +
                            "- Number of active job listings: " + realTimeData.get("totalJobs") + "\n" +
                            "- Number of job categories: " + realTimeData.get("totalCategories") + "\n" +
                            "- Number of upcoming events: " + realTimeData.get("totalEvents") + "\n";

            if (realTimeData.containsKey("userPreferences")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> prefs = (Map<String, Object>) realTimeData.get("userPreferences");

                enhancedPrompt += language.equals("vi") ?
                        "\n# Sở thích của người dùng:\n" :
                        "\n# User preferences:\n";

                if (prefs.containsKey("skills")) {
                    enhancedPrompt += language.equals("vi") ?
                            "- Kỹ năng: " + prefs.get("skills") + "\n" :
                            "- Skills: " + prefs.get("skills") + "\n";
                }

                if (prefs.containsKey("location")) {
                    enhancedPrompt += language.equals("vi") ?
                            "- Vị trí: " + prefs.get("location") + "\n" :
                            "- Location: " + prefs.get("location") + "\n";
                }

                if (prefs.containsKey("field")) {
                    enhancedPrompt += language.equals("vi") ?
                            "- Lĩnh vực: " + prefs.get("field") + "\n" :
                            "- Field: " + prefs.get("field") + "\n";
                }

                if (prefs.containsKey("minSalary") && prefs.containsKey("maxSalary")) {
                    enhancedPrompt += language.equals("vi") ?
                            "- Mức lương: " + prefs.get("currency") + " " + prefs.get("minSalary") +
                                    " - " + prefs.get("currency") + " " + prefs.get("maxSalary") + "\n" :
                            "- Salary range: " + prefs.get("currency") + " " + prefs.get("minSalary") +
                                    " - " + prefs.get("currency") + " " + prefs.get("maxSalary") + "\n";
                }
            }

            if (realTimeData.containsKey("recommendedJobs")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> jobs = (List<Map<String, Object>>) realTimeData.get("recommendedJobs");

                enhancedPrompt += language.equals("vi") ?
                        "\n# Danh sách việc làm đề xuất:\n" :
                        "\n# Recommended job listings:\n";

                for (Map<String, Object> job : jobs) {
                    enhancedPrompt += language.equals("vi") ?
                            "- ID: " + job.get("id") + ", Tiêu đề: " + job.get("title") +
                                    ", Công ty: " + job.get("company") +
                                    ", Mức lương: " + job.get("salary") +
                                    ", Địa điểm: " + job.get("location") +
                                    ", Kỹ năng yêu cầu: " + job.get("skills") +
                                    ", Xem chi tiết: " + job.get("viewUrl") +
                                    ", Ứng tuyển: " + job.get("applyUrl") + "\n" :
                            "- ID: " + job.get("id") + ", Title: " + job.get("title") +
                                    ", Company: " + job.get("company") +
                                    ", Salary: " + job.get("salary") +
                                    ", Location: " + job.get("location") +
                                    ", Required skills: " + job.get("skills") +
                                    ", View details: " + job.get("viewUrl") +
                                    ", Apply: " + job.get("applyUrl") + "\n";
                }

                enhancedPrompt += language.equals("vi") ?
                        "\nKhi đề xuất việc làm cụ thể, hãy sử dụng định dạng phong phú bằng cách thêm " + RICH_MARKUP + "job|123" + RICH_MARKUP +
                                " và thêm liên kết chi tiết bằng cách sử dụng chính xác cú pháp " + LINK_MARKUP + "Xem chi tiết|https://utecareer.edu.vn/jobs/123" + LINK_MARKUP +
                                " hoặc " + LINK_MARKUP + "Ứng tuyển ngay|https://utecareer.edu.vn/jobs/123/apply" + LINK_MARKUP +
                                " (không thêm hoặc bỏ bất kỳ ký tự nào trong cú pháp này)" :
                        "\nWhen recommending specific jobs, use rich formatting by adding " + RICH_MARKUP + "job|123" + RICH_MARKUP +
                                " and add detail links using the exact syntax " + LINK_MARKUP + "View details|https://utecareer.edu.vn/jobs/123" + LINK_MARKUP +
                                " or " + LINK_MARKUP + "Apply now|https://utecareer.edu.vn/jobs/123/apply" + LINK_MARKUP +
                                " (do not add or remove any characters in this syntax)";
            }

            if (realTimeData.containsKey("upcomingEvents")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> events = (List<Map<String, Object>>) realTimeData.get("upcomingEvents");

                enhancedPrompt += language.equals("vi") ?
                        "\n# Sự kiện sắp tới:\n" :
                        "\n# Upcoming events:\n";

                for (Map<String, Object> event : events) {
                    enhancedPrompt += language.equals("vi") ?
                            "- ID: " + event.get("id") + ", Tiêu đề: " + event.get("title") +
                                    ", Ngày: " + event.get("date") +
                                    ", Địa điểm: " + event.get("location") +
                                    ", Tổ chức: " + event.get("organizer") + "\n" :
                            "- ID: " + event.get("id") + ", Title: " + event.get("title") +
                                    ", Date: " + event.get("date") +
                                    ", Location: " + event.get("location") +
                                    ", Organizer: " + event.get("organizer") + "\n";
                }

                enhancedPrompt += language.equals("vi") ?
                        "\nKhi đề cập đến sự kiện cụ thể, hãy sử dụng định dạng phong phú bằng cách thêm " + RICH_MARKUP + "event|{id}" + RICH_MARKUP + "\n" :
                        "\nWhen mentioning specific events, use rich formatting by adding " + RICH_MARKUP + "event|{id}" + RICH_MARKUP + "\n";
            }

            if (realTimeData.containsKey("matchingEmployers")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> employers = (List<Map<String, Object>>) realTimeData.get("matchingEmployers");

                enhancedPrompt += language.equals("vi") ?
                        "\n# Công ty phù hợp:\n" :
                        "\n# Matching companies:\n";

                for (Map<String, Object> employer : employers) {
                    enhancedPrompt += language.equals("vi") ?
                            "- ID: " + employer.get("id") + ", Tên: " + employer.get("name") +
                                    ", Ngành: " + employer.get("industry") +
                                    ", Địa điểm: " + employer.get("location") +
                                    ", Quy mô: " + employer.get("size") +
                                    ", Website: " + employer.get("website") + "\n" :
                            "- ID: " + employer.get("id") + ", Name: " + employer.get("name") +
                                    ", Industry: " + employer.get("industry") +
                                    ", Location: " + employer.get("location") +
                                    ", Size: " + employer.get("size") +
                                    ", Website: " + employer.get("website") + "\n";
                }

                enhancedPrompt += language.equals("vi") ?
                        "\nKhi đề cập đến công ty cụ thể, hãy sử dụng định dạng phong phú bằng cách thêm " + RICH_MARKUP + "employer|{id}" + RICH_MARKUP +
                                " và có thể thêm liên kết đến trang web của công ty bằng cách sử dụng " + LINK_MARKUP + "Website|{website}" + LINK_MARKUP + "\n" :
                        "\nWhen mentioning specific companies, use rich formatting by adding " + RICH_MARKUP + "employer|{id}" + RICH_MARKUP +
                                " and optionally add a link to the company's website using " + LINK_MARKUP + "Website|{website}" + LINK_MARKUP + "\n";
            }
        enhancedPrompt += language.equals("vi") ?
                "\n# Lịch sử trò chuyện:\n" :
                "\n# Conversation history:\n";

        List<Map<String, Object>> history = sessionHistories.getOrDefault(sessionId, new ArrayList<>());
        for (Map<String, Object> message : history) {
            String role = (String) message.get("role");
            String content = (String) message.get("content");

            enhancedPrompt += language.equals("vi") ?
                    "- " + (role.equals("user") ? "Người dùng" : "Trợ lý") + ": " + content + "\n" :
                    "- " + (role.equals("user") ? "User" : "Assistant") + ": " + content + "\n";
        }

        Map<String, Object> requestBody = new HashMap<>();
        String finalEnhancedPrompt = enhancedPrompt;
        requestBody.put("contents", new Object[]{
                new HashMap<String, Object>() {{
                    put("role", "user");
                    put("parts", new Object[]{
                            new HashMap<String, String>() {{
                                put("text", finalEnhancedPrompt);
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

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.2);
        generationConfig.put("maxOutputTokens", 800);
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

        return language.equals("vi") ?
                "Xin lỗi, tôi không thể xử lý yêu cầu của bạn vào lúc này." :
                "Sorry, I cannot process your request at this time.";
    } catch (Exception e) {
        log.error("Error calling Gemini API: {}", e.getMessage());

        return language.equals("vi") ?
                "Xin lỗi, đã xảy ra lỗi khi xử lý yêu cầu của bạn." :
                "Sorry, an error occurred while processing your request.";
    }
    }

    @MessageMapping("/chatbot.check")
    public void checkChatbotStatus(@Payload Map<String, Object> payload) {
        String sessionId = payload.containsKey("sessionId") ?
                payload.get("sessionId").toString() :
                UUID.randomUUID().toString();

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("status", "available");
        response.put("timestamp", LocalDateTime.now().toString());

        messagingTemplate.convertAndSend(
                "/chatbot/" + sessionId + "/status",
                response
        );
    }

    @MessageMapping("/chatbot.clear")
    public void clearChatHistory(@Payload Map<String, Object> payload) {
        if (payload.containsKey("sessionId")) {
            String sessionId = payload.get("sessionId").toString();

            sessionHistories.remove(sessionId);
            sessionLastActivity.put(sessionId, LocalDateTime.now());

            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", sessionId);
            response.put("status", "cleared");
            response.put("message", "Chat history has been cleared");

            messagingTemplate.convertAndSend(
                    "/chatbot/" + sessionId,
                    response
            );

            log.info("Cleared chat history for session: {}", sessionId);
        }
    }

    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 3600000)
    public void scheduledCleanup() {
        log.info("Running scheduled cleanup of expired sessions");
        cleanUpExpiredSessions();
    }

    private Map<String, Object> handleSystemError(String sessionId, String errorMessage, String language) {
        log.error("System error in chat session {}: {}", sessionId, errorMessage);

        String errorResponse = language.equals("vi") ?
                "Xin lỗi, hệ thống đang gặp sự cố. Vui lòng thử lại sau hoặc liên hệ hỗ trợ qua email support@utecareer.edu.vn." :
                "Sorry, the system is experiencing issues. Please try again later or contact support at support@utecareer.edu.vn.";

        Message botMessage = new Message();
        botMessage.setContent(errorResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("message", botMessage);
        response.put("language", language);
        response.put("error", true);

        return response;
    }
}
