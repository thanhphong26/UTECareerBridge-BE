package com.pn.career.services;

import com.pn.career.dtos.UserActivityDTO;
import com.pn.career.models.ActionType;
import com.pn.career.models.UserActivityLog;
import com.pn.career.repositories.JobRepository;
import com.pn.career.repositories.UserActivityLogRepository;
import com.pn.career.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivityLogService implements IUserActivityLog {

    private final UserActivityLogRepository userActivityLogRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

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

}
