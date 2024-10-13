package com.pn.career.services;

import com.pn.career.responses.EmployerResponse;
import com.pn.career.responses.StudentResponse;

import java.util.List;

public interface IFollowerService {
    void createFollower(Integer studentId, Integer employerId);
    void unFollow(Integer studentId, Integer employerId);
    boolean isFollowing(Integer studentId, Integer employerId);
    int getFollowerCount(Integer employerId);
    int getFollowingCount(Integer studentId);
    List<EmployerResponse> getFollowedEmployers(Integer studentId);
    List<StudentResponse> getFollowers(Integer employerId);
}
