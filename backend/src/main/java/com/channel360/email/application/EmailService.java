package com.channel360.email.application;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendResetPasswordEmail(String to, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject("Reset your Channel360 password");

            String html = buildResetEmailHtml(resetLink);

            helper.setText(html, true);

            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String buildResetEmailHtml(String resetLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body style="margin:0;padding:0;background-color:#f5f5f5;font-family:Inter,-apple-system,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px 16px;">
                    <tr>
                      <td align="center">
                        <table width="480" cellpadding="0" cellspacing="0" style="background:#fff;border-radius:12px;box-shadow:0 2px 12px rgba(0,0,0,0.08);overflow:hidden;">
                          <tr>
                            <td style="padding:32px 32px 0 32px;border-top:4px solid #f59e0b;">
                              <h1 style="margin:0 0 4px 0;font-size:20px;font-weight:700;color:#111827;">
                                Reset your password
                              </h1>
                              <p style="margin:0 0 24px 0;font-size:14px;color:#6b7280;line-height:1.5;">
                                You requested a password reset for your Channel360 account.
                                Click the button below to set a new password.
                              </p>
                              <a href="%s"
                                 style="display:inline-block;padding:12px 28px;background:#f59e0b;color:#fff;font-size:14px;font-weight:600;text-decoration:none;border-radius:8px;">
                                Reset Password
                              </a>
                              <p style="margin:24px 0 0 0;font-size:12px;color:#9ca3af;line-height:1.5;">
                                This link will expire in 30 minutes.
                                If you did not request this, please ignore this email.
                              </p>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:24px 32px 32px 32px;border-top:1px solid #e5e7eb;">
                              <p style="margin:0;font-size:11px;color:#9ca3af;">
                                &copy; 2025 Channel360 &bull; Channel Data Management Platform
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(resetLink);
    }
}
