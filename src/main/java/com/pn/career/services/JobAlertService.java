package com.pn.career.services;

import com.pn.career.dtos.JobAlertDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.PermissionDenyException;
import com.pn.career.models.*;
import com.pn.career.repositories.*;
import com.pn.career.responses.JobAlertResponse;
import com.pn.career.responses.JobResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobAlertService implements IJobAlertService {
    private static final Logger log = LoggerFactory.getLogger(JobAlertService.class);
    private final JobAlertRepository jobAlertRepository;
    private final EmailService emailService;
    private final INotificationService notificationService;
    private final UserRepository userRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final JobRepository jobRepository;
    private final StudentRepository studentRepository;

    @Override
    public JobAlertResponse createJobAlert(JobAlertDTO jobAlertDTO, Integer userId) {
        JobAlert jobAlert = new JobAlert();
        JobCategory jobCategoryOptional=null;
        if(jobAlertDTO.getJobCategoryId() != null){
            Optional<JobCategory> jobCategoryOptional1 = jobCategoryRepository.findById(jobAlertDTO.getJobCategoryId());
            jobCategoryOptional =jobCategoryOptional1.get();
        }
        jobAlert.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng")));
        jobAlert.setJobTitle(jobAlertDTO.getJobTitle());
        jobAlert.setLevel(convertListToString(jobAlertDTO.getLevel()));
        jobAlert.setLocation(jobAlertDTO.getLocation());
        jobAlert.setMinSalary(jobAlertDTO.getMinSalary());
        jobAlert.setCompanyField(convertListToString(jobAlertDTO.getCompanyField()));
        jobAlert.setJobCategory(jobCategoryOptional);
        jobAlert.setFrequency(FrequencyEnum.valueOf(jobAlertDTO.getFrequency()));
        jobAlert.setNotifyByEmail(jobAlertDTO.isNotifyByEmail());
        jobAlert.setNotifyByApp(jobAlertDTO.isNotifyByApp());
        jobAlert.setActive(true);

        JobAlert savedJobAlert = jobAlertRepository.save(jobAlert);
        log.info("Saved job alert: {}", savedJobAlert);
        return JobAlertResponse.from(savedJobAlert);
    }

    @Override
    public JobAlertResponse updateJobAlert(Long id, JobAlertDTO jobAlertDTO, Integer userId) {
        JobAlert jobAlert = jobAlertRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông báo việc làm"));
        //check authorization
        if (jobAlert.getUser().getUserId() != userId) {
            throw new PermissionDenyException("Bạn không có quyền truy cập vào thông báo việc làm này");
        }
        JobCategory jobCategoryOptional=null;
        if(jobAlertDTO.getJobCategoryId() != null){
            Optional<JobCategory> jobCategoryOptional1 = jobCategoryRepository.findById(jobAlertDTO.getJobCategoryId());
            jobCategoryOptional =jobCategoryOptional1.get();
        }
        jobAlert.setJobTitle(jobAlertDTO.getJobTitle());
        jobAlert.setLevel(convertListToString(jobAlertDTO.getLevel()));
        jobAlert.setLocation(jobAlertDTO.getLocation());
        jobAlert.setMinSalary(jobAlertDTO.getMinSalary());
        jobAlert.setCompanyField(convertListToString(jobAlertDTO.getCompanyField()));
        jobAlert.setJobCategory(jobCategoryOptional);
        jobAlert.setFrequency(FrequencyEnum.valueOf(jobAlertDTO.getFrequency()));
        jobAlert.setNotifyByEmail(jobAlertDTO.isNotifyByEmail());
        jobAlert.setNotifyByApp(jobAlertDTO.isNotifyByApp());

        JobAlert updatedJobAlert = jobAlertRepository.save(jobAlert);

        return JobAlertResponse.from(updatedJobAlert);
    }

    @Override
    public JobAlertResponse getJobAlertById(Long id) {
        JobAlert jobAlert = jobAlertRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông báo việc làm"));
        return JobAlertResponse.from(jobAlert);
    }

    @Override
    public void deleteJobAlert(Long id, Integer userId) {
        JobAlert jobAlert = jobAlertRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông báo việc làm"));
        //check authorization
        if (jobAlert.getUser().getUserId() != userId) {
            throw new PermissionDenyException("Bạn không có quyền truy cập vào thông báo việc làm này");
        }
        jobAlert.setActive(false);
        jobAlertRepository.save(jobAlert);
    }

    @Override
    public Page<JobAlertResponse> getJobAlertByUserIdAndActive(Integer userId, boolean active, PageRequest pageRequest) {
        return jobAlertRepository.findByUser_UserIdAndActive(userId, active, pageRequest)
                .map(JobAlertResponse::from);
    }

    @Override
    public List<JobResponse> getJobAlertByUser(JobAlertDTO jobAlertDTO, Integer userId) {
        List<JobResponse> jobResponses = new ArrayList<>();

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Job> jobs = jobRepository.searchJobNotifications(userId, jobAlertDTO.getJobTitle(),
                jobAlertDTO.getMinSalary(), jobAlertDTO.getLevel(), jobAlertDTO.getLocation(),
                jobAlertDTO.getJobCategoryId(), jobAlertDTO.getCompanyField(), pageRequest);
        jobResponses = jobs.stream().map(JobResponse::fromJob).toList();

        return jobResponses;
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void processDailyAlert() {
        List<JobAlert> jobAlerts = jobAlertRepository.findByFrequencyAndActive(FrequencyEnum.DAILY, true);
        processAlerts(jobAlerts);
    }
    @Scheduled(cron = "0 0 8 * * 1")
    public void processWeeklyAlert() {
        List<JobAlert> jobAlerts = jobAlertRepository.findByFrequencyAndActive(FrequencyEnum.WEEKLY, true);
        processAlerts(jobAlerts);
    }

    private void processAlerts(List<JobAlert> alerts) {
        for (JobAlert alert : alerts) {
            // Convert to list level and company field
            List<Integer> levelIds = convertStringToList(alert.getLevel());
            List<Integer> companyFieldIds = convertStringToList(alert.getCompanyField());

            PageRequest pageRequest = PageRequest.of(0, 10);
            Page<Job> jobs = jobRepository.searchJobNotifications(alert.getUser().getUserId(), alert.getJobTitle(),
                    alert.getMinSalary(), levelIds, alert.getLocation(),
                    alert.getJobCategory() != null ? alert.getJobCategory().getJobCategoryId() : null, companyFieldIds, pageRequest);
            List<JobResponse> jobResponses = jobs.stream().map(JobResponse::fromJob).toList();

            if (!jobResponses.isEmpty()) {
                if (alert.isNotifyByEmail()) {
                    Student student = studentRepository.findById(alert.getUser().getUserId())
                            .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng"));
                    try {
                        emailService.sendSuitableJobEmail(alert.getUser().getEmail(), student, jobResponses);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }

                if (alert.isNotifyByApp()) {
                    String title = "Thông báo việc làm dựa theo yêu cầu";

                    // Create a detailed HTML message with inline CSS styling
                    StringBuilder contentBuilder = new StringBuilder();
                    contentBuilder.append("<div style='font-family: Arial, sans-serif; padding: 10px;'>");
                    contentBuilder.append("<p style='font-size: 16px; margin-bottom: 15px;'>Có ")
                            .append(jobResponses.size())
                            .append(" việc làm phù hợp với tiêu chí của bạn:</p>");

                    // Add information about each job to the notification content with styling
                    for (int i = 0; i < jobResponses.size(); i++) {
                        JobResponse job = jobResponses.get(i);
                        String jobUrl = "/job/" + job.getJobId(); // Create job detail URL

                        contentBuilder.append("<div style='margin-bottom: 15px; padding: 10px; border-left: 3px solid #3498db; background-color: #f9f9f9;'>");
                        contentBuilder.append("<h3 style='margin: 0 0 8px 0; color: #2c3e50;'><a href='")
                                .append(jobUrl)
                                .append("' style='text-decoration: none; color: #3498db;' target='_blank'>")
                                .append(job.getJobTitle())
                                .append("</a></h3>");
                        contentBuilder.append("<p style='margin: 5px 0; font-size: 14px;'><strong>Công ty:</strong> ")
                                .append(job.getEmployerResponse().getCompanyName())
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
                    }

                    contentBuilder.append("<p style='font-size: 14px; margin-top: 20px;'>Nhấn vào tiêu đề công việc để xem chi tiết.</p>");
                    contentBuilder.append("</div>");

                    String message = contentBuilder.toString();

                    // Pass the job list ID references in the data field for navigation purposes
                    Map<String, Object> data = new HashMap<>();
                    data.put("jobAlertId", alert.getId());
                    data.put("jobIds", jobResponses.stream().map(JobResponse::getJobId).collect(Collectors.toList()));
                    data.put("type", "job_alert");

                    notificationService.sendNotificationForJobAlert(title, message, alert.getUser().getUserId(), data);
                }
            }
        }
    }

    private List<Integer> convertStringToList(String str){
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(str.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
    private String convertListToString(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
