package com.pn.career.services;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Job;
import com.pn.career.models.SaveJob;
import com.pn.career.models.SaveJobId;
import com.pn.career.models.Student;
import com.pn.career.repositories.JobRepository;
import com.pn.career.repositories.SaveJobRepository;
import com.pn.career.repositories.StudentRepository;
import com.pn.career.responses.JobResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveJobService implements ISaveJobService {
    private final SaveJobRepository saveJobRepository;
    private final JobRepository jobRepository;
    private final StudentRepository studentRepository;

    @Override
    public void saveJob(Integer studentId, Integer jobId) {
        Student student=studentRepository.findById(studentId).orElseThrow(()-> new DataNotFoundException("Không tìm thấy thông tin sinh viên"));
        Job job = jobRepository.findById(jobId).orElseThrow(()-> new DataNotFoundException("Không tìm thấy thông tin công việc"));
        SaveJob saveJob = SaveJob.builder()
                .id(SaveJobId.builder()
                        .studentId(studentId)
                        .jobId(jobId)
                        .build())
                .student(student)
                .job(job)
                .build();
        saveJobRepository.save(saveJob);
    }

    @Override
    public void unsaveJob(Integer studentId, Integer jobId) {
        SaveJobId saveJobId = SaveJobId.builder()
                .studentId(studentId)
                .jobId(jobId)
                .build();
        saveJobRepository.deleteById(saveJobId);
    }

    @Override
    public boolean isSaved(Integer studentId, Integer jobId) {
        SaveJobId saveJobId = SaveJobId.builder()
                .studentId(studentId)
                .jobId(jobId)
                .build();
        return saveJobRepository.existsById(saveJobId);
    }

    @Override
    public Page<JobResponse> getSavedJobs(Integer studentId, PageRequest pageRequest) {
        Page<SaveJob> saveJobs = saveJobRepository.findAllByStudent_UserId(studentId, pageRequest);
        return saveJobs.map(saveJob -> JobResponse.fromJob(saveJob.getJob()));
    }
}
