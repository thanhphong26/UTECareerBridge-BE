package com.pn.career.controllers;

import com.pn.career.responses.UserResponse;
import com.pn.career.services.IExportService;
import com.pn.career.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.List;
@RestController
@RequestMapping("${api.prefix}/export")
@RequiredArgsConstructor
public class ExportController {
    private final IUserService userService;
    private final IExportService exportService;
    @GetMapping("/users/pdf")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Resource> exportUsersToPdf(  @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
                                                       @RequestParam(name = "role", defaultValue = "") String role,
                                                       @RequestParam(name="sorting", required = false, defaultValue = "createdAt") String sorting,
                                                       @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                       @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserResponse> users = userService.getAllUsers(keyword, role, sorting, pageRequest);
        List<UserResponse> userResponses = users.getContent();
        ByteArrayInputStream bis=exportService.exportUserToPdf(userResponses);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.pdf");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(bis.available())
                .body(new InputStreamResource(bis));
    }
}
