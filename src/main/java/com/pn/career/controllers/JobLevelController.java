package com.pn.career.controllers;

import com.pn.career.models.JobLevel;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IJobLevelService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/job-levels")
@AllArgsConstructor
public class JobLevelController {
    private final IJobLevelService jobLevelService;
    @GetMapping("/get-all-job-levels")
    public ResponseEntity<ResponseObject> getAllJobLevels(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated=authentication!=null && authentication.isAuthenticated();
        boolean isAdmin=isAuthenticated && authentication.getAuthorities().stream()
                .anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
        List<JobLevel> jobLevels=jobLevelService.findAllJobLevels(isAdmin);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .data(jobLevels)
                .status(HttpStatus.OK)
                .message("Lấy danh sách cấp bậc công việc thành công")
                .build());
    }
}
