package com.pn.career.controllers;

import com.pn.career.dtos.UserActivityDTO;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IUserActivityLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/activity")
@RequiredArgsConstructor
public class UserActivityLogController {
    private final IUserActivityLog userActivityLogService;
    @PostMapping("/log")
    public ResponseEntity<ResponseObject> logUserActivity(@RequestBody UserActivityDTO userActivityDTO) {
        try {
            userActivityLogService.saveUserActivityLog(userActivityDTO);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Thông tin được lưu thành công")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Đã có lỗi xảy ra trong lúc thực hiện thao tác")
                    .build());
        }
    }
}
