package com.pn.career.controllers;

import com.pn.career.models.JobCategory;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IJobCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/job-categories")
@AllArgsConstructor
public class JobCategoryController {
    private final IJobCategoryService jobCategoryService;
    @GetMapping("/get-all-job-categories")
    public ResponseEntity<ResponseObject> getAllJobCategories(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
        boolean isAdmin = isAuthenticated && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<JobCategory> jobCategories = jobCategoryService.findAllJobCategories(isAdmin);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy danh sách các ngành nghề thành công")
                .data(jobCategories)
                .build());
    }
}
