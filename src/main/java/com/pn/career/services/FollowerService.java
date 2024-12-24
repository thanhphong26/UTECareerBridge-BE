package com.pn.career.services;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Employer;
import com.pn.career.models.Follower;
import com.pn.career.models.FollowerId;
import com.pn.career.models.Student;
import com.pn.career.repositories.EmployerRepository;
import com.pn.career.repositories.FollowerRepository;
import com.pn.career.repositories.JobRepository;
import com.pn.career.repositories.StudentRepository;
import com.pn.career.responses.EmployerResponse;
import com.pn.career.responses.StudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowerService implements IFollowerService{
    private final FollowerRepository followerRepository;
    private final StudentRepository studentRepository;
    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;

    @Override
    public void createFollower(Integer studentId, Integer employerId) {
        Student student=studentRepository.findById(studentId).orElseThrow(()->
                new DataNotFoundException("Khôg tìm thấy sinh viên"));
        Employer employer=employerRepository.findById(employerId).orElseThrow(()-> new DataNotFoundException("Không tìm thấy nhà tuyển dụng"));
        Follower follower=Follower.builder()
                .id(FollowerId.builder()
                        .studentId(studentId)
                        .employerId(employerId)
                        .build())
                .student(student)
                .employer(employer)
                .build();
        followerRepository.save(follower);
    }

    @Override
    public void unFollow(Integer studentId, Integer employerId) {
        followerRepository.deleteById(FollowerId.builder()
                .studentId(studentId)
                .employerId(employerId)
                .build());
    }

    @Override
    public boolean isFollowing(Integer studentId, Integer employerId) {
        return followerRepository.existsById(FollowerId.builder()
                .studentId(studentId)
                .employerId(employerId)
                .build());
    }

    @Override
    public int getFollowerCount(Integer employerId) {
        return followerRepository.countByEmployer_UserId(employerId);
    }

    @Override
    public int getFollowingCount(Integer studentId) {
        return followerRepository.countByStudent_UserId(studentId);
    }

    @Override
    public Page<EmployerResponse> getFollowedEmployers(Integer studentId, PageRequest pageRequest) {
        Page<Employer> employers=followerRepository.findByStudent_UserId(studentId, pageRequest);
        return employers.map(employer -> {
            EmployerResponse employerResponse=EmployerResponse.fromUser(employer);
            employerResponse.setCountJob(jobRepository.countByEmployer_UserId(employer.getUserId()));
            employerResponse.setCountFollower(followerRepository.countByEmployer_UserId(employer.getUserId()));
            return employerResponse;
        });
    }

    @Override
    public Page<StudentResponse> getFollowers(Integer employerId, PageRequest pageRequest) {
        Page<Student> students=followerRepository.findByEmployer_UserId(employerId, pageRequest);
        return students.map(StudentResponse::fromUser);
    }
}
