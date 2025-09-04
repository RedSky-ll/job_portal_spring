package ir.parsakav.jobportal.core.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// com/example/sjp/core/mail/MailTestController.java
@RestController
@RequiredArgsConstructor
@RequestMapping("/dev")
public class MailTestController {
    private final MailService mail;

    @GetMapping("/mail")
    public Map<String, String> test(@RequestParam String to) {
        mail.sendText(to, "SJP test", "Hello from Smart Job Portal!");
        return Map.of("status", "sent");
    }
}
