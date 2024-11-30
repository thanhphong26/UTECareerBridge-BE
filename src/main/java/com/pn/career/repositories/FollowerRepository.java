package com.pn.career.repositories;

import com.pn.career.models.Employer;
import com.pn.career.models.Follower;
import com.pn.career.models.FollowerId;
import com.pn.career.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {
    int countByEmployer_UserId(int employerId);
    int countByStudent_UserId(int studentId);
    List<Student> findByEmployer_UserId(int employerId);
    @Query("SELECT f.employer FROM Follower f WHERE f.student.userId = :studentId")
    List<Employer> findByStudent_UserId(@Param("studentId") int studentId);
}
