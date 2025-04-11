package com.pn.career.event;

import com.pn.career.dtos.UserActivityDTO;
import com.pn.career.models.ActionType;
import com.pn.career.services.UserActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobEventListener {
    private final UserActivityLogService userActivityLogService;
    @Async
    @EventListener
    public void handleJobViewedEvent(JobViewedEvent event) {
        UserActivityDTO userActivityDTO = UserActivityDTO.builder()
                .userId(event.getUserId())
                .jobId(event.getJobId())
                .actionType("VIEW")
                .build();
        userActivityLogService.saveUserActivityLog(userActivityDTO);
    }
    @Async
    @EventListener
    public void handleSaveJobEvent(JobSavedEvent event) {
        UserActivityDTO userActivityDTO = UserActivityDTO.builder()
                .userId(event.getUserId())
                .jobId(event.getJobId())
                .actionType("SAVE")
                .build();
        userActivityLogService.saveUserActivityLog(userActivityDTO);
    }
    @Async
    @EventListener
    public void handleUnsavedJobEvent(JobUnsavedEvent event) {
        userActivityLogService.deleteUserActivityLog(event.getUserId(), event.getJobId(), ActionType.valueOf("SAVE"));
    }
    @Async
    @EventListener
    public void handleApplyJobEvent(JobAppliedEvent event) {
        UserActivityDTO userActivityDTO = UserActivityDTO.builder()
                .userId(event.getUserId())
                .jobId(event.getJobId())
                .actionType("APPLY")
                .build();
        userActivityLogService.saveUserActivityLog(userActivityDTO);
    }
}
