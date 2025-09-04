package ir.parsakav.jobportal.core.mail;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void sendText(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        System.out.println("Run");
        msg.setTo(to);
        msg.setFrom("Parsakavianpour@gmail.com");
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }


    public void sendHtml(String to, String subject, String html) throws MessagingException {
        MimeMessage mm = mailSender.createMimeMessage();
        MimeMessageHelper h = new MimeMessageHelper(mm, "UTF-8");
        h.setTo(to);
        System.out.println("Sanaz");
        h.setFrom("Parsakavianpour@gmail.com");
        h.setSubject(subject);
        h.setText(html, true); // HTML enabled
        mailSender.send(mm);
    }
}
