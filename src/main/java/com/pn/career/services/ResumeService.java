package com.pn.career.services;

import com.pn.career.dtos.ResumeDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.PermissionDenyException;
import com.pn.career.models.JobLevel;
import com.pn.career.models.Resume;
import com.pn.career.models.Student;
import com.pn.career.repositories.JobLevelRepository;
import com.pn.career.repositories.ResumeRepository;
import com.pn.career.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService implements IResumeService {
    private final ResumeRepository resumeRepository;
    private final JobLevelRepository jobLevelRepository;
    private final CloudinaryService cloudinaryService;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public Resume createResume(Integer studentId, ResumeDTO resumeDTO) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy sinh viên"));
        JobLevel jobLevel = jobLevelRepository.findById(resumeDTO.getLevelId()).orElseThrow(() -> new DataNotFoundException("Không tìm thấy cấp độ công việc"));
        Resume resume = Resume.builder()
                .resumeTitle(resumeDTO.getResumeTitle())
                .resumeDescription(resumeDTO.getResumeDescription())
                .resumeFile(resumeDTO.getResumeFile())
                .student(student)
                .jobLevel(jobLevel)
                .build();
        return resumeRepository.save(resume);
    }

    @Override
    public List<Resume> getResumesByStudentId(Integer studentId) {
        return resumeRepository.findAllByStudent_UserId(studentId);
    }

    @Override
    public Resume getResumeById(Integer studentId, Integer resumeId) {
        Resume resume=resumeRepository.findById(resumeId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy hồ sơ"));
        if(resume.getStudent().getUserId()!=studentId){
            throw new PermissionDenyException("Bạn không có quyền thực hiện chức năng này");
        }
        return resume;
    }

    @Override
    public Resume updateResume(Integer resumeId, ResumeDTO resumeDTO) {
        return null;
    }

    @Override
    public void deleteResume(Integer studentId, Integer resumeId) {
        Resume resume=getResumeById(studentId, resumeId);
        if(resume.getStudent().getUserId()!=studentId){
            throw new PermissionDenyException("Bạn không có quyền thực hiện chức năng này");
        }
        resumeRepository.deleteById(resumeId);
    }

    @Override
    public boolean updateActiveResume(Integer resumeId, Integer studentId, boolean isActive) {
        Resume resume=getResumeById(studentId, resumeId);
        List<Resume> resumes=resumeRepository.findAllByStudent_UserId(studentId);
        //inactive resume if student has another active resume
        if(isActive){
            for(Resume r:resumes){
                if(r.isActive() && r.getResumeId()!=resumeId){
                    r.setActive(false);
                    resumeRepository.save(r);
                }
            }
        }
        if(resume.getStudent().getUserId()!=studentId){
            throw new PermissionDenyException("Bạn không có quyền thực hiện chức năng này");
        }
        resume.setActive(isActive);
        resumeRepository.save(resume);
        return true;
    }
}
