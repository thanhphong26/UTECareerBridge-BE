package com.pn.career.controllers;

import com.pn.career.dtos.InterviewEvaluationDTO;
import com.pn.career.dtos.InterviewRequestDTO;
import com.pn.career.models.Interview;
import com.pn.career.models.InterviewEvaluation;
import com.pn.career.models.InterviewStatus;
import com.pn.career.responses.InterviewEvaluationResponse;
import com.pn.career.responses.InterviewResponse;
import com.pn.career.responses.MeetingResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.GoogleCalendarService;
import com.pn.career.services.IInterviewEvaluationService;
import com.pn.career.services.IInterviewService;
import com.pn.career.services.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


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
    private final IInterviewEvaluationService interviewEvaluationService;

    @GetMapping("/student/evaluation")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getInterviewEvaluationByStudentId(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<InterviewEvaluationResponse> interviewEvaluations = interviewEvaluationService.getAllInterviewEvaluationByStudentId(userId, pageRequest);
        return ResponseEntity.ok()
                .body(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Lấy danh sách đánh giá phỏng vấn thành công")
                        .data(interviewEvaluations)
                        .build());
    }
    @PostMapping("/evaluation")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> createInterviewEvaluation(@RequestBody InterviewEvaluationDTO interviewEvaluationDTO, @AuthenticationPrincipal Jwt jwt) {
            Long userIdLong = jwt.getClaim("userId");
            Integer userId = userIdLong != null ? userIdLong.intValue() : null;
            InterviewEvaluationResponse interviewEvaluation = interviewEvaluationService.createInterviewEvaluation(interviewEvaluationDTO);
            return ResponseEntity.ok()
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Đánh giá ứng viên thành công")
                            .data(interviewEvaluation)
                            .build());

    }
    @PutMapping("/evaluation/{interviewId}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> updateInterviewEvaluation(@PathVariable Integer interviewId, @RequestBody InterviewEvaluationDTO interviewEvaluationDTO) {
        try {
            InterviewEvaluationResponse updatedInterviewEvaluation = interviewEvaluationService.updateInterviewEvaluation(interviewId, interviewEvaluationDTO);
            return ResponseEntity.ok()
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Cập nhật đánh giá phỏng vấn thành công")
                            .data(updatedInterviewEvaluation)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi khi cập nhật đánh giá phỏng vấn")
                            .data(null)
                            .build());
        }
    }
    @GetMapping("/evaluation/job/{jobId}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getInterviewEvaluationByJobId(@PathVariable Integer jobId) {
        try {
            List<InterviewEvaluationResponse> interviewEvaluation = interviewEvaluationService.getAllInterviewEvaluationByJobId(jobId);
            return ResponseEntity.ok()
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Lấy thông tin đánh giá phỏng vấn thành công")
                            .data(interviewEvaluation)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi khi lấy thông tin đánh giá phỏng vấn")
                            .data(null)
                            .build());
        }
    }
    @GetMapping("/evaluation/{interviewId}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getInterviewEvaluationByInterviewId(@PathVariable Integer interviewId) {
        try {
            InterviewEvaluationResponse interviewEvaluation = interviewEvaluationService.getInterviewEvaluationByInterviewId(interviewId);
            return ResponseEntity.ok()
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Lấy thông tin đánh giá phỏng vấn thành công")
                            .data(interviewEvaluation)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi khi lấy thông tin đánh giá phỏng vấn")
                            .data(null)
                            .build());
        }
    }
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
    @GetMapping("/{interviewId}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getInterviewById(@PathVariable Integer interviewId) {
        try {
            InterviewResponse interview = interviewService.getInterviewById(interviewId);
            return ResponseEntity.ok()
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Lấy thông tin cuộc phỏng vấn thành công")
                            .data(interview)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi khi lấy thông tin cuộc phỏng vấn")
                            .data(null)
                            .build());
        }
    }
    @PutMapping("/{interviewId}/status")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> updateInterviewStatus(@PathVariable Integer interviewId, @AuthenticationPrincipal Jwt jwt, @RequestParam String status) {
        try {
            Long userIdLong = jwt.getClaim("userId");
            Integer userId = userIdLong != null ? userIdLong.intValue() : null;
            if (status == null || status.isEmpty() || !status.matches("SCHEDULED|IN_PROGRESS|COMPLETED|CANCELED|POSTPONED")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Trạng thái cập nhật không hợp lệ")
                                .data(null)
                                .build());
            }
            interviewService.updateInterviewStatus(interviewId, InterviewStatus.valueOf(status));
            return ResponseEntity.ok()
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Cập nhật trạng thái cuộc phỏng vấn thành công")
                            .data(null)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi khi cập nhật trạng thái cuộc phỏng vấn")
                            .data(null)
                            .build());
        }
    }
    @GetMapping("/employers/calendar")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getEmployerCalendar(@AuthenticationPrincipal Jwt jwt, @RequestParam(required = false) String page, @RequestParam(required = false) String size) {
        try {
            Long userIdLong = jwt.getClaim("userId");
            Integer userId = userIdLong != null ? userIdLong.intValue() : null;
            PageRequest pageRequest = PageRequest.of(
                    page != null ? Integer.parseInt(page) : 0,
                    size != null ? Integer.parseInt(size) : 10
            );
            Page<InterviewResponse> interviews = interviewService.getInterviewsByEmployerId(userId, pageRequest);
            return ResponseEntity.ok()
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Lấy danh sách lịch phỏng vấn thành công")
                            .data(interviews)
                            .build());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi khi lấy danh sách lịch phỏng vấn")
                            .data(Collections.emptyList())
                            .build());
        }
    }
}