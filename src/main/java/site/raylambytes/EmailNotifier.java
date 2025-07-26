package site.raylambytes;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class EmailNotifier {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotifier.class);// for demo pu


    public static void sendEmail(String subject, String body) {
        logger.info("Preparing to send an email..");
        String from = ConfigLoader.get("email.from");
        String password = ConfigLoader.get("email.password");
        String to = ConfigLoader.get("email.to");

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
            throw new RuntimeException("❌ Failed to send email: " + e.getMessage(), e);
        }
    }
}
