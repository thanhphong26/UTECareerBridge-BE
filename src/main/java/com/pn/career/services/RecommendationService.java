package com.pn.career.services;

import com.pn.career.responses.JobRecommendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String RECOMMENDATION_API_URL = "http://localhost:8000/api/recommend";

    public List<JobRecommendResponse> getRecommendationsForUser(int userId) {
        // 1. Chuẩn bị dữ liệu request
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_id", userId);
        requestBody.put("top_n", 5);
        log.info("Request body: {}", requestBody);
        try {
            // 2. Gọi FastAPI Recommendation Service
            log.info("Calling recommendation service...");
            ResponseEntity<List<JobRecommendResponse>> response =
                    restTemplate.exchange(
                            RECOMMENDATION_API_URL,
                            HttpMethod.POST,
                            new HttpEntity<>(requestBody),
                            new ParameterizedTypeReference<List<JobRecommendResponse>>() {}

                    );
            log.info("Recommendation service response: {}", response);
            return response.getBody();
        } catch (RestClientException e) {
            // Xử lý nếu gặp lỗi
            log.error("Recommendation service error", e);
            return Collections.emptyList();
        }
    }
}
