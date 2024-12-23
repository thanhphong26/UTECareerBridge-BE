package com.pn.career.services;

import com.pn.career.dtos.InterviewDTO;
import com.pn.career.models.Job;
import com.pn.career.models.Resume;
import com.pn.career.models.Student;
import com.pn.career.models.User;
import com.pn.career.responses.ApplicationResponse;
import com.pn.career.responses.JobResponse;
import com.pn.career.responses.ResumeResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class EmailService {
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    SpringTemplateEngine thymeleafTemplateEngine;
    @Value("${MAIL_SERVICE_USERNAME}")
    private String username;

    public void sendForgotPasswordEmail(String recipientEmail, String name, String resetUrl) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setFrom("UTE-Career-Bridge <" + username + ">");

        helper.setTo(recipientEmail);
        helper.setSubject("Tạo mật khẩu đăng nhập mới trên UTE-Career-Bridge");

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("resetUrl", resetUrl);
        String emailContent = thymeleafTemplateEngine.process("forgot-password", context);

        helper.setText(emailContent, true); // true indicates HTML

        mailSender.send(mimeMessage);
    }
    //send job application email
    public void sendJobApplicationEmail(String recipientEmail, ResumeResponse resume, JobResponse job, ApplicationResponse applicationResponse) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setFrom("UTE-Career-Bridge <" + username + ">");
        helper.setTo(recipientEmail);
        helper.setSubject("Thông báo ứng tuyển");

        Context context = new Context();
        context.setVariable("application", applicationResponse);
        context.setVariable("job", job);
        context.setVariable("resume", resume);
        String emailContent = thymeleafTemplateEngine.process("job-apply-successful", context);

        helper.setText(emailContent, true); // true indicates HTML

        mailSender.send(mimeMessage);
    }
    //send suitable job for student
    public void sendSuitableJobEmail(String recipientEmail, Student student, List<JobResponse> jobs) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setFrom("UTE-Career-Bridge <" + username + ">");
        helper.setTo(recipientEmail);
        helper.setSubject("Thông báo việc làm phù hợp");

        Context context = new Context();
        context.setVariable("student", student);
        context.setVariable("jobs", jobs);
        String emailContent = thymeleafTemplateEngine.process("job-recommendations", context);

        helper.setText(emailContent, true); // true indicates HTML

        mailSender.send(mimeMessage);
    }
    public void sendMailReplyAcceptInterview(String recipientEmail, String studentName, JobResponse job,InterviewDTO interviewDTO) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setFrom("UTE-Career-Bridge <" + username + ">");
        helper.setTo(recipientEmail);
        helper.setSubject("Thông báo phỏng vấn");

        Context context = new Context();
        context.setVariable("studentName", studentName);
        context.setVariable("job", job);
        context.setVariable("interview", interviewDTO);
        String emailContent = thymeleafTemplateEngine.process("job-reply-accept", context);

        helper.setText(emailContent, true); // true indicates HTML

        mailSender.send(mimeMessage);
    }
    public void sendMailReject(String recipientEmail, String studentName, JobResponse job) throws MessagingException{
        MimeMessage mimeMessage=mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(mimeMessage,"UTF-8");
        helper.setFrom("UTE-Career-Bridge <"+username+">");
        helper.setTo(recipientEmail);
        helper.setSubject("Thông báo kết quả ứng tuyển");

        Context context=new Context();
        context.setVariable("studentName",studentName);
        context.setVariable("job",job);
        String emailContent=thymeleafTemplateEngine.process("job-reply-reject",context);
        helper.setText(emailContent,true);
        mailSender.send(mimeMessage);
    }
}
