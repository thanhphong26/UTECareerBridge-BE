package com.pn.career.services;

import com.pn.career.dtos.StudentDTO;
import com.pn.career.models.Student;
import com.pn.career.models.User;
import com.pn.career.repositories.StudentRepository;
import com.pn.career.repositories.UserRepository;
import com.pn.career.responses.StudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class StudentService implements IStudentService{
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
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
}
