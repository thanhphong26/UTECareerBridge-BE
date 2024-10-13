package com.pn.career.repositories;

import com.pn.career.models.Employer;
import com.pn.career.models.Follower;
import com.pn.career.models.FollowerId;
import com.pn.career.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {
    int countByEmployer_UserId(int employerId);
    int countByStudent_UserId(int studentId);
    List<Student> findByEmployer_UserId(int employerId);
    List<Employer> findByStudent_UserId(int studentId);
}
