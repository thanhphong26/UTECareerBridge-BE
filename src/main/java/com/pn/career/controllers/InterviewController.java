package com.pn.career.controllers;

import com.pn.career.dtos.InterviewRequestDTO;
import com.pn.career.responses.MeetingResponse;
import com.pn.career.services.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("${api.prefix}/interviews")
@RequiredArgsConstructor
public class InterviewController {
    private final ZoomController zoomController;
    private final GoogleCalendarService googleCalendarService;

    @PostMapping("/schedule")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<?> scheduleInterview(@RequestBody InterviewRequestDTO request, @AuthenticationPrincipal Jwt jwt) {
        try {
            Long userIdLong = jwt.getClaim("userId");
            Integer userId = userIdLong != null ? userIdLong.intValue() : null;
            // 1. Tạo cuộc họp Zoom
            ResponseEntity<MeetingResponse> zoomResponse = zoomController.createMeeting(jwt);
            log.info("Zoom response: {}", zoomResponse);
            if (!zoomResponse.getStatusCode().equals(HttpStatus.OK) || zoomResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể tạo cuộc họp Zoom: " +
                                (zoomResponse.getBody() != null ? zoomResponse.getBody().getMessage() : "Lỗi không xác định"));
            }

            MeetingResponse meetingInfo = zoomResponse.getBody();
            log.info("Meeting response: {}", meetingInfo);
            // 2. Tạo sự kiện trên Google Calendar
            String calendarEventId = googleCalendarService.createCalendarEventAsEmployer(
                    request, meetingInfo, userId);
            log.info("Calendar event ID: {}", calendarEventId);
            // 3. Cập nhật thông tin vào đối tượng MeetingResponse
            meetingInfo.setCalendarEventId(calendarEventId);

            // 4. Có thể lưu thông tin lịch phỏng vấn vào database tại đây

            return ResponseEntity.ok(meetingInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lên lịch phỏng vấn: " + e.getMessage());
        }
    }
}