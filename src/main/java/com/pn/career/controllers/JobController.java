package com.pn.career.controllers;

import com.pn.career.dtos.JobDTO;
import com.pn.career.responses.JobListResponse;
import com.pn.career.responses.JobResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IJobService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            JobResponse job=jobService.createJob(employerId,jobDTO);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Tạo bài đăng công việc thành công")
                    .data(job)
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .build());
        }
    }
    @GetMapping("/{jobId}")
    public ResponseEntity<ResponseObject> getJobById(@PathVariable Integer jobId){
        JobResponse jobResponse = jobService.getJobById(jobId).orElseThrow(() -> new RuntimeException("Không tìm thấy công việc"));
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thông tin công việc thành công")
                .status(HttpStatus.OK)
                .data(jobResponse)
                .build());
    }
    @PutMapping("/employer/job-posting/{jobId}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> updateJob(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer jobId, @RequestBody JobDTO jobDTO){
        try{
            Long userIdLong = jwt.getClaim("userId");
            Integer employerId = userIdLong != null ? userIdLong.intValue() : null;
            JobResponse job=jobService.updateJob(employerId,jobId,jobDTO);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Cập nhật công việc thành công")
                    .data(job)
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .build());
        }
    }
    @GetMapping("/employers/all-jobs")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getAllJobsByEmployer(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit){
        Long userIdLong = jwt.getClaim("userId");
        Integer employerId = userIdLong != null ? userIdLong.intValue() : null;
        int totalPages=0;
        PageRequest pageRequest=PageRequest.of(page,limit);
        Page<JobResponse> jobs=jobService.getJobsByEmployerId(employerId,pageRequest);
        if(jobs.getTotalPages()>0){
            totalPages=jobs.getTotalPages();
        }
        List<JobResponse> jobResponses=jobs.getContent();
        if(jobResponses.isEmpty()){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Không có công việc nào")
                    .build());
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy danh sách công việc thành công")
                .data(JobListResponse.builder()
                        .jobResponses(jobResponses)
                        .totalPages(totalPages)
                        .build())
                .build());
    }
    @PutMapping("/employer/job-posting/hide/{jobId}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> hideJob(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer jobId){
        Long userIdLong = jwt.getClaim("userId");
        Integer employerId = userIdLong != null ? userIdLong.intValue() : null;
        JobResponse jobResponse=jobService.hideJob(employerId,jobId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Ẩn công việc thành công")
                .data(jobResponse)
                .build());
    }
    @GetMapping("/admin/all-jobs")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getAllJobs(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit){
        int totalPages=0;
        PageRequest pageRequest=PageRequest.of(page,limit);
        Page<JobResponse> jobs=jobService.getAllJobs(pageRequest);
        if(jobs.getTotalPages()>0){
            totalPages=jobs.getTotalPages();
        }
        List<JobResponse> jobResponses=jobs.getContent();
        if(jobResponses.isEmpty()){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Không có công việc nào")
                    .build());
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy danh sách công việc thành công")
                .data(JobListResponse.builder()
                        .jobResponses(jobResponses)
                        .totalPages(totalPages)
                        .build())
                .build());
    }
    @PutMapping("/admin/job-approval/approve/{jobId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> approveJob(@PathVariable Integer jobId){
        JobResponse jobResponse=jobService.approveJob(jobId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Duyệt công việc thành công")
                .data(jobResponse)
                .build());
    }
    @PutMapping("/admin/job-approval/reject/{jobId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> rejectJob(@PathVariable Integer jobId, @RequestParam String reasonReject){
        JobResponse jobResponse=jobService.rejectJob(jobId,reasonReject);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Từ chối công việc thành công")
                .data(jobResponse)
                .build());
    }
}
