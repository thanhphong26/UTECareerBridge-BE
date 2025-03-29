package com.pn.career.controllers;

import com.pn.career.responses.JobRecommendResponse;
import com.pn.career.responses.JobResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController
@RequestMapping("${api.prefix}/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    @GetMapping("/jobs/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getRecommendations(@PathVariable Integer userId) {
        // 1. Tiếp nhận request
        // 2. Gọi service để lấy đề xuất
        List<JobRecommendResponse> recommendations =
                recommendationService.getRecommendationsForUser(userId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(recommendations)
                .message("Lấy danh sách công việc hệ thống đề xuất thành công")
                .build());
    }
}
