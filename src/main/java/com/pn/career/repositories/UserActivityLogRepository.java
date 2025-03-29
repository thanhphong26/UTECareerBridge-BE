package com.pn.career.repositories;

import com.pn.career.models.ActionType;
import com.pn.career.models.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    UserActivityLog findUserActivityLogByUser_UserIdAndJob_JobIdAndActionType(Integer userId, Integer jobId, ActionType actionType);
}
