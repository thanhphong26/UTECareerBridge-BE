package com.pn.career.repositories;

import com.pn.career.models.ActionType;
import com.pn.career.models.User;
import com.pn.career.models.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    UserActivityLog findUserActivityLogByUser_UserIdAndJob_JobIdAndActionType(Integer userId, Integer jobId, ActionType actionType);
    List<UserActivityLog> findByUser_UserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Integer userId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    @Query("SELECT u FROM UserActivityLog u WHERE u.user.userId = :userId AND u.createdAt BETWEEN :startDate AND :endDate ORDER BY u.createdAt DESC")
    List<UserActivityLog> findUserActivitiesByDateRange(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
