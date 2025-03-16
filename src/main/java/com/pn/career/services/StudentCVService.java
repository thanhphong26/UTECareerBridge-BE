package com.pn.career.services;

import com.pn.career.models.StudentCV;
import com.pn.career.repositories.StudentCVRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentCVService implements IStudentCVService {
    private final StudentCVRepository studentCVRepository;

    @Override
    public StudentCV createStudentCV(StudentCV studentCV) {
        return studentCVRepository.save(studentCV);
    }

    @Override
    public List<StudentCV> findAllByStudentId(Integer studentId) {
        return studentCVRepository.findAllByStudent_UserId(studentId);
    }

    @Override
    public StudentCV updateStudentCV(StudentCV studentCV) {
        return studentCVRepository.save(studentCV);
    }

    @Override
    public void deleteStudentCV(StudentCV studentCV) {
        studentCVRepository.delete(studentCV);
    }
}
