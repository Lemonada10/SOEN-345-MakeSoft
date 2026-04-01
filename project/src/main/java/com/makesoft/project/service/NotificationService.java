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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.makesoft.project.model.User;

/**
 * Sends sign-up confirmation by email.
 * <p>
 * When {@code spring.mail.host} is set, SMTP is used first so messages can go to any recipient
 * (e.g. Gmail app password). Otherwise {@code RESEND_API_KEY} is used; with Resend's default
 * {@code onboarding@resend.dev} you can only send to your own Resend account email — verify a
 * domain at resend.com and set {@code RESEND_FROM} to a sender on that domain to reach everyone.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final String CONFIRM_SUBJECT = "Account created – MakeSoft";
    private static final String CONFIRM_EMAIL_BODY = "Welcome! Your account has been created successfully. You can now sign in to reserve tickets.";
    private static final String RESEND_API_URL = "https://api.resend.com/emails";
    private static final String DEFAULT_RESEND_FROM = "MakeSoft <onboarding@resend.dev>";

    private final MailSender mailSender;
    private final RestTemplate restTemplate;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${RESEND_API_KEY:}")
    private String resendApiKey;

    /** Sender for Resend, e.g. {@code MakeSoft <noreply@your-verified-domain.com>} */
    @Value("${RESEND_FROM:}")
    private String resendFrom;

    public NotificationService(
            @Autowired(required = false) MailSender mailSender,
            @Autowired(required = false) RestTemplate restTemplate) {
        this.mailSender = mailSender;
        this.restTemplate = restTemplate != null ? restTemplate : new RestTemplate();
    }

    public void sendSignUpConfirmation(User user, String confirmBy) {
        if (user.getEmail() == null || user.getEmail().isBlank()) return;
        sendConfirmationEmail(user.getEmail().trim(), user.getName());
    }

    private void sendConfirmationEmail(String to, String name) {
        to = to.trim();
        if (to.isEmpty()) return;

        String body = "Hi " + (name != null && !name.isBlank() ? name.trim() : "there") + ",\n\n" + CONFIRM_EMAIL_BODY;

        boolean smtpReady = mailSender != null && mailHost != null && !mailHost.isBlank();
        boolean resendReady = resendApiKey != null && !resendApiKey.isBlank();

        // Prefer SMTP when configured — delivers to any address (unlike Resend test domain).
        if (smtpReady) {
            sendEmailViaSpringMail(to, body);
            return;
        }
        if (resendReady) {
            sendEmailViaResend(to, body);
            return;
        }
        log.info("Mail not configured (set spring.mail.* or RESEND_API_KEY); would send sign-up confirmation to {}", to);
    }

    private String resendFromAddress() {
        if (resendFrom != null && !resendFrom.isBlank()) {
            return resendFrom.trim();
        }
        return DEFAULT_RESEND_FROM;
    }

    private void sendEmailViaResend(String to, String textBody) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey.trim());
            Map<String, Object> body = Map.of(
                    "from", resendFromAddress(),
                    "to", List.of(to),
                    "subject", CONFIRM_SUBJECT,
                    "text", textBody);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForObject(RESEND_API_URL, request, Map.class);
            log.info("Sign-up confirmation email sent to {} via Resend", to);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String detail = e.getResponseBodyAsString();
            log.error("Resend API {} for recipient {}: {}", e.getStatusCode(), to, detail);
            if (detail != null && detail.contains("verify a domain")) {
                log.error("Resend test sender only allows mail to your account email. Add a domain at https://resend.com/domains and set RESEND_FROM to e.g. MakeSoft <noreply@yourdomain.com>, or configure spring.mail.* (SMTP) to send to any address.");
            }
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
            log.warn("Failed to send confirmation email to {} via Spring Mail: {}", to, e.getMessage());
        }
    }
}
