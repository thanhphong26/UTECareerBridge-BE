package com.pn.career.controllers;

import com.pn.career.dtos.JobDTO;
import com.pn.career.models.Job;
import com.pn.career.models.JobStatus;
import com.pn.career.responses.JobListResponse;
import com.pn.career.responses.JobResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IJobService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger= LoggerFactory.getLogger(JobController.class);
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
    @GetMapping("/employers/{employerId}/all-jobs")
    public ResponseEntity<ResponseObject> getAllJobsByEmployer(@PathVariable Integer employerId,@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit){
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
    @GetMapping("/get-jobs-by-status")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getJobsByStatus(@AuthenticationPrincipal Jwt jwt, @RequestParam JobStatus jobStatus,
                                                          @RequestParam(defaultValue = "0") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer limit){
        Long userIdLong = jwt.getClaim("userId");
        Integer employerId = userIdLong != null ? userIdLong.intValue() : null;
        int totalPages=0;
        PageRequest pageRequest=PageRequest.of(page,limit);
        Page<JobResponse> jobs=jobService.getJobByStatus(employerId,jobStatus,pageRequest);
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
    public ResponseEntity<ResponseObject> hideJob(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer jobId, @RequestParam JobStatus jobStatus){
        Long userIdLong = jwt.getClaim("userId");
        Integer employerId = userIdLong != null ? userIdLong.intValue() : null;
        JobResponse jobResponse=jobService.hideOrEnableJob(employerId,jobId, jobStatus);
        String message=jobStatus.equals(JobStatus.INACTIVE)? "Ẩn công việc thành công" : "Hiện công việc thành công";
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message(message)
                .data(jobResponse)
                .build());
    }
    @DeleteMapping("/employer/job-posting/delete/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> deleteJob(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer jobId){
        Long userIdLong = jwt.getClaim("userId");
        Integer employerId = userIdLong != null ? userIdLong.intValue() : null;
        jobService.deleteJob(employerId,jobId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Xóa công việc thành công")
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
    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchJobs(@RequestParam(defaultValue = "0") @Min(0) Integer page,
                                                     @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit, @RequestParam String keyword,
                                                     @RequestParam(defaultValue = "0") Integer categoryId, @RequestParam(defaultValue = "0") Integer industryId,
                                                     @RequestParam(defaultValue = "0") Integer jobLevelId, @RequestParam(defaultValue = "0") Integer skillId){
        int totalPages=0;
        PageRequest pageRequest=PageRequest.of(page,limit);
        logger.info("Pagerequest: "+pageRequest);
        Page<JobResponse> jobs=jobService.searchJob(keyword,categoryId,industryId,jobLevelId,skillId,pageRequest);
        logger.info("Jobs: "+jobs.getTotalPages());
        if(jobs.isEmpty()){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Không có công việc nào")
                    .build());
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Tìm kiếm công việc thành công")
                .data(JobListResponse.builder()
                        .jobResponses(jobs.getContent())
                        .totalPages(jobs.getTotalPages())
                        .totalElements(jobs.getTotalElements())
                        .build())
                .build());
    }
}
