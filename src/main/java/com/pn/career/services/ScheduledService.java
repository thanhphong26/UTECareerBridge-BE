package com.pn.career.services;

import com.pn.career.models.Job;
import com.pn.career.models.Student;
import com.pn.career.repositories.StudentRepository;
import com.pn.career.responses.JobResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ScheduledService {
    private final StudentRepository studentRepository;
    private final StudentSkillService studentSkillService;
    private final EmailService emailService;
    private final Logger logger= LoggerFactory.getLogger(ScheduledService.class);
    @Scheduled(cron = "0 07 07 * * *")
    @Transactional
    public void sendSuitableJobEmail() {
        List<Student> students = studentRepository.findAllByRole_RoleName("student");
        for (Student student : students) {
            List<Job> suitableJobs = studentSkillService.getJobsByStudentSkill(student.getUserId());
            List<JobResponse> jobResponses = suitableJobs.stream().map(JobResponse::fromJob).toList();
            try {
                emailService.sendSuitableJobEmail(student.getEmail(), student, jobResponses);
            } catch (MessagingException e) {
                // Handle email sending errors here (e.g., logging)
                logger.info("Error sending email to student with email: " + student.getEmail());
            }
        }
    }
}
