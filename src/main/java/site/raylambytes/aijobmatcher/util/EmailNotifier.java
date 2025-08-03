package site.raylambytes.aijobmatcher.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.raylambytes.aijobmatcher.AppConfig;

import java.util.Properties;

@Service
public class EmailNotifier {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotifier.class);// for demo pu
    @Autowired
    private AppConfig appConfig;

    public void sendEmail(String subject, String body) {
        logger.info("Preparing to send an email..");

        String from = appConfig.getEmailFrom();
        String password = appConfig.getEmailPassword();
        String to = appConfig.getEmailTo();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
            logger.info("✅ Email sent successfully");
        } catch (MessagingException e) {
            logger.error("Error sending email: ", e);
            throw new RuntimeException("❌ Failed to send email: " + e.getMessage(), e);
        }
    }
}
