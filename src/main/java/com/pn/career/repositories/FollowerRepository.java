package com.pn.career.repositories;

import com.pn.career.models.Employer;
import com.pn.career.models.Follower;
import com.pn.career.models.FollowerId;
import com.pn.career.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {
    int countByEmployer_UserId(int employerId);
    int countByStudent_UserId(int studentId);
    Page<Student> findByEmployer_UserId(int employerId, Pageable pageable);
    @Query("SELECT f.employer FROM Follower f WHERE f.student.userId = :studentId")
    Page<Employer> findByStudent_UserId(@Param("studentId") int studentId, Pageable pageable);
    @Query("SELECT f.student FROM Follower f WHERE f.employer.userId = :employerId")
    List<Student> findStudentsByEmployerId(@Param("employerId") Integer employerId);

}
