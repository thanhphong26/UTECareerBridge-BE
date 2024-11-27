package com.pn.career.services;

import com.pn.career.dtos.StudentDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Resume;
import com.pn.career.models.Student;
import com.pn.career.models.User;
import com.pn.career.repositories.ResumeRepository;
import com.pn.career.repositories.StudentRepository;
import com.pn.career.repositories.UserRepository;
import com.pn.career.responses.StudentResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService implements IStudentService{
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ResumeRepository resumeRepository;
    @Override
    public StudentResponse updateStudent(Integer studentId, StudentDTO studentDTO) {
        Student student=studentRepository.findById(studentId).orElseThrow(()-> new RuntimeException("Thông tin sinh viên không tồn tại"));
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setPhoneNumber(studentDTO.getPhoneNumber());
        student.setGender(studentDTO.isGender());
        student.setDob(studentDTO.getDob());
        student.setProvinceId(studentDTO.getProvinceId());
        student.setDistrictId(studentDTO.getDistrictId());
        student.setWardId(studentDTO.getWardId());
        student.setAddress(studentDTO.getAddress());
        student.setUniversityEmail(studentDTO.getUniversityEmail());
        student.setYear(studentDTO.getYear());
        student.setProfileImage(studentDTO.getProfileImage());
        student.setCategoryId(studentDTO.getCategoryId());
        studentRepository.save(student);
        return StudentResponse.fromStudent(student);
    }

    @Override
    public StudentResponse getStudentById(Integer studentId) {
        return studentRepository.findById(studentId)
                .map(StudentResponse::fromStudent)
                .orElseThrow(()->new RuntimeException("Không tìm thấy sinh viên"));
    }

    @Override
    @Transactional
    public void updateIsFindingJob(Integer studentId, boolean isFindingJob) {
        if(!isFindingJob){
            studentRepository.updateIsFindingJob(studentId,isFindingJob);
            return;
        }
        List<Resume> resume=resumeRepository.findAllByStudent_UserId(studentId);
        if(resume.isEmpty()){
            throw new DataNotFoundException("Bạn chưa update hồ sơ xin việc. Vui lòng thực hiện");
        }
        //Kiểm tra xem hồ sơ có active hay không nếu không có yêu cầu sinh viên update resume trước khi bật tìm việc(is_find)
        if(resume.stream().noneMatch(Resume::isActive)){
            throw new DataNotFoundException("Hồ sơ của bạn chưa được active. Vui lòng thực hiện");
        }
        studentRepository.updateIsFindingJob(studentId,isFindingJob);
    }
}
