package com.pn.career.controllers;

import com.pn.career.dtos.InterviewRequestDTO;
import com.pn.career.models.Interview;
import com.pn.career.responses.MeetingResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.GoogleCalendarService;
import com.pn.career.services.IInterviewService;
import com.pn.career.services.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@Slf4j
@RestController
@RequestMapping("${api.prefix}/interviews")
@RequiredArgsConstructor
public class InterviewController {
    private final ZoomController zoomController;
    private final GoogleCalendarService googleCalendarService;
    private final IInterviewService interviewService;
    private final GoogleOauthController googleOauthController;
    private final INotificationService notificationService;
    @PostMapping("/schedule")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> scheduleInterview(@RequestBody InterviewRequestDTO request, @AuthenticationPrincipal Jwt jwt) {
        try {
            Long userIdLong = jwt.getClaim("userId");
            Integer userId = userIdLong != null ? userIdLong.intValue() : null;
            boolean isGoogleAuthenticated = googleCalendarService.isEmployerGoogleAuthenticated(userId);
            if (!isGoogleAuthenticated) {
                // Lấy URL xác thực từ GoogleOauthController
                ResponseEntity<ResponseObject> authUrl = googleOauthController.getAuthorizationUrl(jwt);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.UNAUTHORIZED)
                                .message("Bạn cần xác thực với Google Calendar trước khi lên lịch phỏng vấn")
                                .data(authUrl.getBody().getData())
                                .build());
            }

            if (request.getLink() == null || request.getLink().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Link không được để trống")
                                .data(null)
                                .build());
            }
            MeetingResponse meetingInfo = new MeetingResponse();
            meetingInfo.setJoinUrl(request.getLink());
            String calendarEventId = googleCalendarService.createCalendarEventAsEmployer(
                    request, meetingInfo, userId);
            meetingInfo.setCalendarEventId(calendarEventId);
            Interview interview = interviewService.saveInterview(request, meetingInfo, userId);
            log.info("Interview ID: {}", interview.getInterviewId());
            Integer studentID = interview.getApplication().getResume().getStudent().getUserId();
            String title = "Lịch phỏng vấn mới từ " + interview.getApplication().getJob().getEmployer().getCompanyName();
            String message = "Bạn đã được lên lịch phỏng vấn cho vị trí " + interview.getApplication().getJob().getJobTitle() +
                    " vào lúc " + interview.getScheduleDate() + ". Thời gian phỏng vấn là " + interview.getDuration() + " phút. Vui lòng kiểm tra email để xác nhận lịch phỏng vấn.";
            notificationService.sendNotificationInterview(title, message, meetingInfo.getJoinUrl(), studentID, interview.getApplication().getJob().getJobTitle(), interview.getApplication().getJob().getEmployer().getCompanyName());
            return ResponseEntity.ok()
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Lên lịch phỏng vấn thành công")
                            .data(interview)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi khi lên lịch phỏng vấn")
                            .data(null)
                            .build());
        }
    }
}