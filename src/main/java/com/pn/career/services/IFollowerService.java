package com.pn.career.services;
import com.pn.career.responses.EmployerResponse;
import com.pn.career.responses.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
public interface IFollowerService {
    void createFollower(Integer studentId, Integer employerId);
    void unFollow(Integer studentId, Integer employerId);
    boolean isFollowing(Integer studentId, Integer employerId);
    int getFollowerCount(Integer employerId);
    int getFollowingCount(Integer studentId);
    Page<EmployerResponse> getFollowedEmployers(Integer studentId, PageRequest pageRequest);
    Page<StudentResponse> getFollowers(Integer employerId, PageRequest pageRequest);
}
