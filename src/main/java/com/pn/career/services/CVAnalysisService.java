package com.pn.career.services;

import com.pn.career.responses.CVAnalysisResponse;
import com.pn.career.responses.CVRecommendationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CVAnalysisService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String CV_API_BASE_URL = "http://localhost:8000/api/cv";

    public CVAnalysisResponse analyzeCV(int resumeId) {
        try {
            // Gọi FastAPI CV Analysis Service với resume_id
            log.info("Calling CV analysis service for resume ID: {}", resumeId);
            ResponseEntity<CVAnalysisResponse> response =
                    restTemplate.getForEntity(
                            CV_API_BASE_URL + "/analyze/" + resumeId,
                            CVAnalysisResponse.class
                    );
            log.info("CV analysis service response: {}", response);
            return response.getBody();
        } catch (RestClientException e) {
            // Xử lý nếu gặp lỗi
            log.error("CV analysis service error", e);
            CVAnalysisResponse errorResponse = new CVAnalysisResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError("Error connecting to CV analysis service: " + e.getMessage());
            return errorResponse;
        }
    }

    public CVAnalysisResponse uploadAndAnalyzeCV(MultipartFile file, Integer studentId) throws IOException {
        try {
            // Chuẩn bị file và parameters cho multipart request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            body.add("file", fileResource);
            if (studentId != null) {
                body.add("student_id", studentId);
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Gọi FastAPI CV Analysis Service
            log.info("Uploading and analyzing CV for student ID: {}", studentId);
            ResponseEntity<CVAnalysisResponse> response =
                    restTemplate.exchange(
                            CV_API_BASE_URL + "/upload",
                            HttpMethod.POST,
                            requestEntity,
                            CVAnalysisResponse.class
                    );
            log.info("CV upload and analysis service response: {}", response);
            return response.getBody();
        } catch (RestClientException e) {
            // Xử lý nếu gặp lỗi
            log.error("CV upload and analysis service error", e);
            CVAnalysisResponse errorResponse = new CVAnalysisResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError("Error connecting to CV analysis service: " + e.getMessage());
            return errorResponse;
        }
    }

    public CVRecommendationResponse getJobRecommendationsForCV(int resumeId, int limit) {
        try {
            // Gọi FastAPI CV Recommendation Service với resume_id
            log.info("Calling job recommendation service for resume ID: {}", resumeId);
            ResponseEntity<CVRecommendationResponse> response =
                    restTemplate.getForEntity(
                            CV_API_BASE_URL + "/recommend/resume/" + resumeId + "?limit=" + limit,
                            CVRecommendationResponse.class
                    );
            log.info("CV job recommendation service response: {}", response);
            return response.getBody();
        } catch (RestClientException e) {
            // Xử lý nếu gặp lỗi
            log.error("CV job recommendation service error", e);
            CVRecommendationResponse errorResponse = new CVRecommendationResponse();
            errorResponse.setRecommendations(Collections.emptyList());
            return errorResponse;
        }
    }

    public CVRecommendationResponse getJobRecommendationsForStudent(int studentId, int limit) {
        try {
            // Gọi FastAPI CV Recommendation Service với student_id
            log.info("Calling job recommendation service for student ID: {}", studentId);
            ResponseEntity<CVRecommendationResponse> response =
                    restTemplate.getForEntity(
                            CV_API_BASE_URL + "/recommend/student/" + studentId + "?limit=" + limit,
                            CVRecommendationResponse.class
                    );
            log.info("CV job recommendation service response: {}", response);
            return response.getBody();
        } catch (RestClientException e) {
            // Xử lý nếu gặp lỗi
            log.error("CV job recommendation service error", e);
            CVRecommendationResponse errorResponse = new CVRecommendationResponse();
            errorResponse.setRecommendations(Collections.emptyList());
            return errorResponse;
        }
    }
}