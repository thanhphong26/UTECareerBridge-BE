package com.pn.career.controllers;

import com.pn.career.dtos.ResumeDTO;
import com.pn.career.models.Application;
import com.pn.career.models.Resume;
import com.pn.career.responses.ApplicationResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.responses.ResumeResponse;
import com.pn.career.services.IApplicationService;
import com.pn.career.services.IResumeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/students")
@RequiredArgsConstructor
public class StudentController {
    private final IResumeService resumeService;
    private final IApplicationService applicationService;
    @PostMapping("/upload/resumes")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> uploadResume(@AuthenticationPrincipal Jwt jwt, @ModelAttribute ResumeDTO resumeDTO) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        Resume resume= resumeService.createResume(studentId, resumeDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(ResumeResponse.fromResume(resume))
                .message("Upload cv thành công")
                .build());

    }
    @GetMapping("/resumes")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getAllResumes(@AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        List<Resume> resumes = resumeService.getResumesByStudentId(studentId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(resumes.stream().map(ResumeResponse::fromResume).toList())
                .message("Lấy danh sách cv thành công")
                .build());
    }
    @GetMapping("/resumes/{resumeId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getResumeById(@PathVariable Integer resumeId) {
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(ResumeResponse.fromResume(resumeService.getResumeById(resumeId)))
                .message("Lấy cv thành công")
                .build());
    }
    @PostMapping("/jobs/apply")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> applyJob(@RequestParam Integer jobId, @RequestParam Integer resumeId) {
        Application application=applicationService.createApplication(jobId, resumeId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(ApplicationResponse.fromApplication(application))
                .message("Ứng tuyển thành công")
                .build());
    }
}
