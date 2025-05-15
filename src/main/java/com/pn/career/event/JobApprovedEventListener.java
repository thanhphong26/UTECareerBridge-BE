package com.pn.career.event;

import com.pn.career.models.Job;
import com.pn.career.models.Notification;
import com.pn.career.models.Student;
import com.pn.career.models.User;
import com.pn.career.repositories.FollowerRepository;
import com.pn.career.repositories.JobRepository;
import com.pn.career.repositories.NotificationRepository;
import com.pn.career.repositories.UserRepository;
import com.pn.career.services.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobApprovedEventListener {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final INotificationService notificationService;

    @Async
    @EventListener
    public void handleJobApprovedEvent(JobApprovedEvent event) {
        try {
            log.info("Processing job approved event for job ID: {}", event.getJobId());
            // Find the job
            Optional<Job> jobOptional = jobRepository.findById(event.getJobId());
            if (jobOptional.isEmpty()) {
                log.error("Job not found with ID: {}", event.getJobId());
                return;
            }

            Job job = jobOptional.get();
            Integer employerId = event.getEmployerId();

            List<Student> followingUsers = followerRepository.findStudentsByEmployerId(employerId);
            if (followingUsers.isEmpty()) {
                log.info("No users follow employer ID: {}", employerId);
                return;
            }
            // Create notification content
            String title = "Nhà tuyển dụng mà bạn theo dõi đã đăng công việc mới";
            String jobUrl = "/job/" + job.getJobId();
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("<div style='font-family: Arial, sans-serif; padding: 10px;'>");
            contentBuilder.append("<div style='margin-bottom: 15px; padding: 10px; border-left: 3px solid #3498db; background-color: #f9f9f9;'>");
            contentBuilder.append("<h3 style='margin: 0 0 8px 0; color: #2c3e50;'><a href='")
                    .append(jobUrl)
                    .append("' style='text-decoration: none; color: #3498db;' target='_blank'>")
                    .append(job.getJobTitle())
                    .append("</a></h3>");
            contentBuilder.append("<p style='margin: 5px 0; font-size: 14px;'><strong>Công ty:</strong> ")
                    .append(job.getEmployer().getCompanyName())
                    .append("</p>");
            contentBuilder.append("<p style='margin: 5px 0; font-size: 14px;'><strong>Vị trí:</strong> ")
                    .append(job.getJobLocation())
                    .append("</p>");
            contentBuilder.append("<p style='margin: 5px 0; font-size: 14px;'><strong>Mức lương:</strong> ")
                    .append(job.getJobMinSalary())
                    .append(" - ")
                    .append(job.getJobMaxSalary())
                    .append("</p>");
            contentBuilder.append("</div>");
            contentBuilder.append("<p style='font-size: 14px; margin-top: 20px;'>Nhấn vào tiêu đề công việc để xem chi tiết.</p>");
            contentBuilder.append("</div>");

            String message = contentBuilder.toString();
            // Create notifications for each follower
            for (User user : followingUsers) {
                notificationService.sendNotificationForStudentFollowEmployer(title, message, user.getUserId());
            }
            log.info("Successfully processed job approved event for job ID: {}", event.getJobId());
        } catch (Exception e) {
            log.error("Error processing job approved event for job ID: " + event.getJobId(), e);
        }

    }
}