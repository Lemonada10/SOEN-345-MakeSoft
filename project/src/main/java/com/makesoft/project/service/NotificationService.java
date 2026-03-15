package com.makesoft.project.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.makesoft.project.model.User;

/**
 * Sends sign-up confirmation by email only.
 * Email: Resend API (RESEND_API_KEY) or Spring Mail (spring.mail.*).
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final String CONFIRM_SUBJECT = "Account created – MakeSoft";
    private static final String CONFIRM_EMAIL_BODY = "Welcome! Your account has been created successfully. You can now sign in to reserve tickets.";
    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    private final MailSender mailSender;
    private final RestTemplate restTemplate;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${RESEND_API_KEY:}")
    private String resendApiKey;

    public NotificationService(
            @Autowired(required = false) MailSender mailSender,
            @Autowired(required = false) RestTemplate restTemplate) {
        this.mailSender = mailSender;
        this.restTemplate = restTemplate != null ? restTemplate : new RestTemplate();
    }

    /**
     * Sends sign-up confirmation email to the user (if they have an email).
     * confirmBy is ignored; only email is sent.
     */
    public void sendSignUpConfirmation(User user, String confirmBy) {
        if (user.getEmail() == null || user.getEmail().isBlank()) return;
        sendConfirmationEmail(user.getEmail(), user.getName());
    }

    private void sendConfirmationEmail(String to, String name) {
        String body = "Hi " + (name != null && !name.isBlank() ? name : "there") + ",\n\n" + CONFIRM_EMAIL_BODY;

        if (resendApiKey != null && !resendApiKey.isBlank()) {
            sendEmailViaResend(to, body);
            return;
        }
        if (mailSender != null && mailHost != null && !mailHost.isBlank()) {
            sendEmailViaSpringMail(to, body);
            return;
        }
        log.info("Mail not configured (set RESEND_API_KEY or spring.mail.*); would send sign-up confirmation to {}", to);
    }

    private void sendEmailViaResend(String to, String textBody) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey.trim());
            Map<String, Object> body = Map.of(
                    "from", "MakeSoft <onboarding@resend.dev>",
                    "to", List.of(to),
                    "subject", CONFIRM_SUBJECT,
                    "text", textBody);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForObject(RESEND_API_URL, request, Map.class);
            log.info("Sign-up confirmation email sent to {} via Resend", to);
        } catch (Exception e) {
            log.warn("Failed to send confirmation email to {} via Resend: {}", to, e.getMessage());
        }
    }

    private void sendEmailViaSpringMail(String to, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(CONFIRM_SUBJECT);
            msg.setText(body);
            mailSender.send(msg);
            log.info("Sign-up confirmation email sent to {} via Spring Mail", to);
        } catch (Exception e) {
            log.warn("Failed to send confirmation email to {}: {}", to, e.getMessage());
        }
    }
}
