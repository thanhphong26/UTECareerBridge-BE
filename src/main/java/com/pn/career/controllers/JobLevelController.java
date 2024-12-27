package com.pn.career.controllers;

import com.pn.career.dtos.JobLevelDTO;
import com.pn.career.models.JobLevel;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IJobLevelService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/job-levels")
@AllArgsConstructor
public class JobLevelController {
    private final IJobLevelService jobLevelService;
    @GetMapping("/{jobLevelId}")
    public ResponseEntity<ResponseObject> getJobLevelById(@PathVariable Integer jobLevelId){
        JobLevel jobLevel=jobLevelService.getJobLevelById(jobLevelId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .data(jobLevel)
                .status(HttpStatus.OK)
                .message("Lấy cấp bậc công việc thành công")
                .build());
    }
    @GetMapping("/get-all-job-levels")
    public ResponseEntity<ResponseObject> getAllJobLevels(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
        boolean isAdmin = isAuthenticated && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<JobLevel> jobLevels=jobLevelService.findAllJobLevels(isAdmin);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .data(jobLevels)
                .status(HttpStatus.OK)
                .message("Lấy danh sách cấp bậc công việc thành công")
                .build());
    }
    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createJobLevel(@RequestBody JobLevelDTO jobLevelDTO){
        JobLevel jobLevel=jobLevelService.createJobLevel(jobLevelDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .data(jobLevel)
                .status(HttpStatus.CREATED)
                .message("Tạo cấp bậc công việc thành công")
                .build());
    }
    @PutMapping("/{jobLevelId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateJobLevel(@PathVariable Integer jobLevelId, @RequestBody JobLevelDTO jobLevelDTO){
        JobLevel jobLevel=jobLevelService.updateJobLevel(jobLevelId, jobLevelDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .data(jobLevel)
                .status(HttpStatus.OK)
                .message("Cập nhật cấp bậc công việc thành công")
                .build());
    }
    @DeleteMapping("/{jobLevelId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteJobLevel(@PathVariable Integer jobLevelId){
        jobLevelService.deleteJobLevel(jobLevelId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Xóa cấp bậc công việc thành công")
                .build());
    }
}
