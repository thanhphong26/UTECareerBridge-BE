package com.pn.career.controllers;

import com.pn.career.responses.CVAnalysisResponse;
import com.pn.career.responses.CVRecommendationResponse;
import com.pn.career.responses.ListResumeJobMatchResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.CVAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("${api.prefix}/cv")
@RequiredArgsConstructor
@Slf4j
public class CVAnalysisController {
    private final CVAnalysisService cvAnalysisService;

    @GetMapping("/job/{jobId}/matching_resumes")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getJobMatchesForResume(@PathVariable Integer jobId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer employerId = userIdLong != null ? userIdLong.intValue() : null;
        ListResumeJobMatchResponse resumeJobMatchResponse = cvAnalysisService.getJobMatchesForResume(jobId, employerId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(resumeJobMatchResponse)
                .message("Job matches for resume retrieved successfully")
                .build());
    }
    @GetMapping("/analyze/{resumeId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> analyzeCV(@PathVariable int resumeId) {
        CVAnalysisResponse analysis = cvAnalysisService.analyzeCV(resumeId);

        if (!analysis.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(analysis.getError() != null ? analysis.getError() : "CV analysis failed")
                            .data(null)
                            .build());
        }

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(analysis)
                .message("CV analysis completed successfully")
                .build());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> uploadAndAnalyzeCV(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "studentId", required = false) Integer studentId
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("CV file cannot be empty")
                                .data(null)
                                .build());
            }

            CVAnalysisResponse analysis = cvAnalysisService.uploadAndAnalyzeCV(file, studentId);

            if (!analysis.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message(analysis.getError() != null ? analysis.getError() : "CV analysis failed")
                                .data(null)
                                .build());
            }

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(analysis)
                    .message("CV uploaded and analyzed successfully")
                    .build());
        } catch (IOException e) {
            log.error("Error processing CV file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error processing CV file: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }

    @GetMapping("/recommend/resume/{resumeId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getJobRecommendationsForCV(
            @PathVariable int resumeId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        CVRecommendationResponse recommendations = cvAnalysisService.getJobRecommendationsForCV(resumeId, limit);
        log.info("Job recommendations for resume ID {}: {}", resumeId, recommendations);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(recommendations)
                .message("Job recommendations for CV retrieved successfully")
                .build());
    }

    @GetMapping("/recommend/student/{studentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getJobRecommendationsForStudent(
            @PathVariable int studentId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        CVRecommendationResponse recommendations = cvAnalysisService.getJobRecommendationsForStudent(studentId, limit);

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(recommendations)
                .message("Job recommendations for student retrieved successfully")
                .build());
    }
}