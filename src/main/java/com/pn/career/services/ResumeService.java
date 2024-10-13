package com.pn.career.services;

import com.pn.career.dtos.ResumeDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.InvalidMultipartFile;
import com.pn.career.models.JobLevel;
import com.pn.career.models.Resume;
import com.pn.career.models.Student;
import com.pn.career.repositories.JobLevelRepository;
import com.pn.career.repositories.ResumeRepository;
import com.pn.career.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ResumeService implements IResumeService{
    private final ResumeRepository resumeRepository;
    private final JobLevelRepository jobLevelRepository;
    private final CloudinaryService cloudinaryService;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public Resume createResume(Integer studentId, ResumeDTO resumeDTO) {
        try {
            Student student = studentRepository.findById(studentId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy sinh viên"));
            JobLevel jobLevel=jobLevelRepository.findById(resumeDTO.getLevelId()).orElseThrow(()->new DataNotFoundException("Không tìm thấy cấp độ công việc"));
            if (!resumeDTO.getResumeFile().isEmpty()) {
                if(!cloudinaryService.isFileValid(resumeDTO.getResumeFile())) {
                    throw new InvalidMultipartFile("Vui lòng upload đúng định dạng được cho phép: file pdf hoặc ảnh.");
                }
                Random random = new Random();
                int randomInt = random.nextInt(1000);
                String publicId = student.getUserId() + "_" + resumeDTO.getResumeFile().getOriginalFilename() + random.nextInt(1000);
                String resumeUrl = cloudinaryService.uploadCvToCloudinary(resumeDTO.getResumeFile(), publicId);
                Resume resume = Resume.builder()
                        .resumeTitle(resumeDTO.getResumeTitle())
                        .resumeDescription(resumeDTO.getResumeDescription())
                        .resumeFile(resumeUrl)
                        .student(student)
                        .jobLevel(jobLevel)
                        .build();
                return resumeRepository.save(resume);
            }
            throw new InvalidMultipartFile("Vui lòng upload CV để tiếp tục cập nhật hồ sơ.");
        } catch (IOException e) {
            throw new RuntimeException("Đã xảy ra lỗi trong quá trình upload. Vui lòng thử lại sau", e);
        }
    }
    @Override
    public List<Resume> getResumesByStudentId(Integer studentId) {
        return resumeRepository.findAllByStudent_UserId(studentId);
    }
    @Override
    public Resume getResumeById(Integer resumeId) {
        return resumeRepository.findById(resumeId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy hồ sơ"));
    }

    @Override
    public Resume updateResume(Integer resumeId, ResumeDTO resumeDTO) {
        return null;
    }
}
