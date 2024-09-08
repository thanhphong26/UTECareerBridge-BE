package com.pn.career.services;

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

        helper.setTo(recipientEmail);
        helper.setSubject("Tạo mật khẩu đăng nhập mới trên UTE-Career-Bridge");

        /*// Prepare email template variables
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("resetUrl", resetUrl);*/

        // Render email template using Thymeleaf
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("resetUrl", resetUrl);
        String emailContent = thymeleafTemplateEngine.process("forgot-password", context);

        helper.setText(emailContent, true); // true indicates HTML

        mailSender.send(mimeMessage);
    }
}
