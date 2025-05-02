package com.pn.career.controllers;

import com.pn.career.dtos.JobAlertDTO;
import com.pn.career.responses.JobAlertResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IJobAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/job-alerts")
@RequiredArgsConstructor
public class JobAlertController {
    private final IJobAlertService jobAlertService;
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getJobAlertById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        JobAlertResponse jobAlertResponse = jobAlertService.getJobAlertById(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thông báo việc làm thành công")
                .status(HttpStatus.OK)
                .data(jobAlertResponse)
                .build());
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getJobAlertByUserIdAndActive(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thông báo việc làm thành công")
                .status(HttpStatus.OK)
                .data(jobAlertService.getJobAlertByUserIdAndActive(userId, true, PageRequest.of(page, size)))
                .build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> createJobAlert(@RequestBody JobAlertDTO jobAlertDTO, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        JobAlertResponse jobAlertResponse = jobAlertService.createJobAlert(jobAlertDTO, userId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tạo thông báo việc làm thành công")
                .status(HttpStatus.OK)
                .data(jobAlertResponse)
                .build());
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> updateJobAlert(@PathVariable Long id, @RequestBody JobAlertDTO jobAlertDTO, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        JobAlertResponse jobAlertResponse = jobAlertService.updateJobAlert(id, jobAlertDTO, userId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Cập nhật thông báo việc làm thành công")
                .status(HttpStatus.OK)
                .data(jobAlertResponse)
                .build());
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> deleteJobAlert(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        jobAlertService.deleteJobAlert(id, userId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Xóa thông báo việc làm thành công")
                .status(HttpStatus.OK)
                .build());
    }
}
