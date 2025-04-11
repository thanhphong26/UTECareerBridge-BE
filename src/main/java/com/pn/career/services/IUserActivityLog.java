package com.pn.career.services;

import com.pn.career.dtos.UserActivityDTO;
import com.pn.career.models.ActionType;
import com.pn.career.models.UserActivityLog;

public interface IUserActivityLog {
    UserActivityLog findUserActivityLogByUser_UserIdAndJob_JobId(Integer userId, Integer jobId, ActionType actionType);
    void saveUserActivityLog(UserActivityDTO userActivityDTO);
    void deleteUserActivityLog(Integer userId, Integer jobId, ActionType actionType);
}
