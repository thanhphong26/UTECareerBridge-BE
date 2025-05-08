package com.pn.career.services;

import com.pn.career.dtos.UserActivityDTO;
import com.pn.career.models.*;
import com.pn.career.repositories.*;
import com.pn.career.responses.StudentActivityStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserActivityLogService implements IUserActivityLog {

    private final UserActivityLogRepository userActivityLogRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final InterviewRepository interviewRepository;
    private final EmployerRepository employerRepository;

    @Override
    public UserActivityLog findUserActivityLogByUser_UserIdAndJob_JobId(Integer userId, Integer jobId, ActionType actionType) {
        return userActivityLogRepository.findUserActivityLogByUser_UserIdAndJob_JobIdAndActionType(userId, jobId, actionType);
    }

    @Override
    public void saveUserActivityLog(UserActivityDTO userActivityDTO) {
        if(userActivityLogRepository.findUserActivityLogByUser_UserIdAndJob_JobIdAndActionType(userActivityDTO.getUserId(), userActivityDTO.getJobId(), ActionType.valueOf(userActivityDTO.getActionType())) != null) {
            return;
        }
        UserActivityLog userActivityLog = new UserActivityLog();
        userActivityLog.setUser(userRepository.findById(userActivityDTO.getUserId()).get());
        userActivityLog.setJob(jobRepository.findById(userActivityDTO.getJobId()).get());
        userActivityLog.setActionType(ActionType.valueOf(userActivityDTO.getActionType()));
        userActivityLogRepository.save(userActivityLog);
    }

    @Override
    public void deleteUserActivityLog(Integer userId, Integer jobId, ActionType actionType) {
        UserActivityLog userActivityLogToDelete = findUserActivityLogByUser_UserIdAndJob_JobId(userId, jobId, actionType);
        if(userActivityLogToDelete != null) {
            userActivityLogRepository.delete(userActivityLogToDelete);
        }
    }

    @Override
    public List<StudentActivityStatsResponse> getStudentActivities(Integer userId, LocalDate startDate, LocalDate endDate) {
        List<StudentActivityStatsResponse> activities = new ArrayList<>();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<UserActivityLog> userActivities = userActivityLogRepository.findUserActivitiesByDateRange(
                userId, startDateTime, endDateTime);

        for (UserActivityLog activity : userActivities) {
            String type = convertActionTypeToString(activity.getActionType());
            String description = getActivityDescription(activity);

            activities.add(StudentActivityStatsResponse.builder()
                    .type(type)
                    .description(description)
                    .date(activity.getCreatedAt())
                    .build());
        }

        List<Interview> interviews = interviewRepository.findInterviewsByStudentIdAndDateRange(
                userId, startDateTime, endDateTime);

        // Convert interviews to response objects
        for (Interview interview : interviews) {
            activities.add(StudentActivityStatsResponse.builder()
                    .type("interview")
                    .description(getInterviewDescription(interview))
                    .date(interview.getCreatedAt())
                    .build());
        }

        // Get resumes
        List<Resume> resumes = resumeRepository.findResumesByStudentIdAndDateRange(
                userId, startDateTime, endDateTime);

        for (Resume resume : resumes) {
            activities.add(StudentActivityStatsResponse.builder()
                    .type("resume")
                    .description("Cập nhật hồ sơ ứng tuyển: " + resume.getResumeTitle())
                    .date(resume.getCreatedAt())
                    .build());
        }

        return activities.stream()
                .sorted(Comparator.comparing(StudentActivityStatsResponse::getDate).reversed())
                .collect(Collectors.toList());
    }
    private String convertActionTypeToString(ActionType actionType) {
        switch (actionType) {
            case VIEW:
                return "viewed";
            case APPLY:
                return "applied";
            case SAVE:
                return "saved";
            default:
                return actionType.toString().toLowerCase();
        }
    }

    private String getActivityDescription(UserActivityLog activity) {
        String jobTitle = activity.getJob() != null ? activity.getJob().getJobTitle() : "Unknown job";

        switch (activity.getActionType()) {
            case VIEW:
                return "Bạn đã xem công việc " + jobTitle;
            case APPLY:
                return "Bạn đã ứng tuyển vị trí " + jobTitle;
            case SAVE:
                return "Bạn đã lưu công việc " + jobTitle + " vào danh sách yêu thích";
            default:
                return "Bạn đã thực hiện hành động " + activity.getActionType() + " với công việc " + jobTitle;
        }
    }

    private String getInterviewDescription(Interview interview) {
        String jobTitle = "Unknown position";
        Employer employer = employerRepository.findById(interview.getEmployerId()).orElse(null);
        if (interview.getApplication() != null && interview.getApplication().getJob() != null) {
            jobTitle = interview.getApplication().getJob().getJobTitle();
        }
        switch (interview.getStatus()) {
            case SCHEDULED:
                return "Bạn có lịch phỏng vấn cho công việc" + jobTitle + " với công ty " + employer.getCompanyName();
            case COMPLETED:
                return "Cuộc phỏng vấn công việc " + jobTitle + "với công ty " + employer.getCompanyName() + " đã hoàn thành";
            case CANCELLED:
                return "Cuộc phỏng vấn cho công việc " + jobTitle + " đã bị hủy";
            default:
                return "Cuộc phỏng vấn cho công việc " + jobTitle + " đã được lên lịch";
        }
    }

}
