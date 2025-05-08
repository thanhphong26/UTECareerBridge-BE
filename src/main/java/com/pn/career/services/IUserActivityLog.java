package com.pn.career.services;

import com.pn.career.dtos.UserActivityDTO;
import com.pn.career.models.ActionType;
import com.pn.career.models.UserActivityLog;
import com.pn.career.responses.StudentActivityStatsResponse;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

public interface IUserActivityLog {
    UserActivityLog findUserActivityLogByUser_UserIdAndJob_JobId(Integer userId, Integer jobId, ActionType actionType);
    void saveUserActivityLog(UserActivityDTO userActivityDTO);
    void deleteUserActivityLog(Integer userId, Integer jobId, ActionType actionType);
    List<StudentActivityStatsResponse> getStudentActivities(Integer userId, LocalDate startDate, LocalDate endDate);
}
