package com.pn.career.controllers;

import com.pn.career.dtos.JobDTO;
import com.pn.career.models.Job;
import com.pn.career.responses.JobResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IJobService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/jobs")
@AllArgsConstructor
public class JobController {
    private final IJobService jobService;
    @PostMapping("/job-posting/new-job")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> createJobPosting(@AuthenticationPrincipal Jwt jwt, @RequestBody JobDTO jobDTO){
        try{
            Long userIdLong = jwt.getClaim("userId");
            Integer employerId = userIdLong != null ? userIdLong.intValue() : null;
            Job job=jobService.createJob(employerId,jobDTO);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Tạo bài đăng công việc thành công")
                    .data(JobResponse.fromJob(job))
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .build());
        }
    }
}
